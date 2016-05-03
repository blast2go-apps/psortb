package com.biobam.b2gapps.psortb.algo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bioinfo.commons.io.utils.FileUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.algo.internal.PsortbResultParser;
import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.B2GJob;
import com.biobam.blast2go.api.job.input.ItemsOrderList;
import com.biobam.blast2go.api.scm.IResultFSHandler;
import com.biobam.blast2go.api.scm.IServiceCloud;
import com.biobam.blast2go.api.scm.IServiceCloudParameters;
import com.biobam.blast2go.api.scm.RequestPack;
import com.biobam.blast2go.api.scm.SimpleServiceCloudImpl;
import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;

import es.blast2go.data.IProject;

public class PsortbAlgo extends B2GJob<PsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(PsortbAlgo.class);

	public PsortbAlgo() {
		super("PSORTb", new PsortbParameters());
	}

	private final static String SERVICE_NAME = "psortb";
	private IServiceCloud service;

	enum PSParameters {
		P_ORGANISM("organism"),
		P_GRAM("gram"),
		P_ADVANCED_GRAM("advancedgram"),
		P_CUTOFF("cutoff"),
		P_FILE_NAME("filename");
		private String key;

		private PSParameters(final String key) {
			this.key = key;
		}

		public String key() {
			return key;
		}
	}

	@Override
	public void run() throws InterruptedException {
		final IProject project = getInput(PsortbJobMetadata.INPUT_PROJECT);
		final ItemsOrderList orderList = getInput(PsortbJobMetadata.ADDITIONAL_ORDER_LIST);

		if (project.getSelectedSequencesCount() == 0) {
			setFinishMessage("No sequences selected.");
			return;
		}
		if (!project.atLeastOneSequenceComplies(SeqCondImpl.COND_HAS_SEQ_STRING)) {
			setFinishMessage("There are no sequences with sequence information, please load the original fasta-file and try again.");
			return;
		}

		beginTask(getName(), project.getSelectedSequencesCount());

		final PsortbParameters parameters = getParameters();

		// Fill the PSORTb object map with the available sequences.
		final Map<String, PsortbEntry> parseResult = new LinkedHashMap<String, PsortbEntry>();
		Iterator<ILightSequence> iterator = project.onlySelectedSequencesIterator(orderList);
		while (iterator.hasNext() && !isCanceled()) {
			final ILightSequence seq = iterator.next();
			parseResult.put(seq.getName(), PsortbEntry.createEmpty(seq.getName()));
		}
		final PsortbObject resultObject = PsortbObject.newInstance("PSORTb Results" + addInputObjectNames(), parseResult);
		// Post the object, it will be updated with results during the execution.
		postOutputResults(resultObject);

		//		int sequencesProcessedCount = 0;
		int lengthCount = 0;
		final int LENGTH_LIMIT = 2000;
		File file;
		try {
			file = File.createTempFile("fasta", ".fasta");
		} catch (final IOException e) {
			log.error("Could not create temporary file. Algorithm will stop.", e);
			throw new IllegalStateException("Could not create temporary file. Algorithm will stop");
		}

		service = new SimpleServiceCloudImpl();

		service.setPackResultHandler(new IResultFSHandler() {
			int count = 0;

			@Override
			public void handleResult(final FileSystem fs) {
				final Path path = fs.getPath("output.txt");
				final Collection<PsortbEntry> entries = PsortbResultParser.parseResult(path, getIProgressMonitor());
				for (final PsortbEntry entry : entries) {
					resultObject.updateEntry(entry);
				}
				count += entries.size();
				postJobMessage(count + "/" + project.getSelectedSequencesCount() + " sequence results obtained...");
			}
		});

		final IServiceCloudParameters scParameters = IServiceCloudParameters.create();
		scParameters.put(PSParameters.P_ORGANISM.key(), parameters.organism.getValue()
		        .getId());
		scParameters.put(PSParameters.P_GRAM.key(), parameters.gram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_ADVANCED_GRAM.key(), parameters.advancedGram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_CUTOFF.key(), String.valueOf(parameters.cutoff.getValue()));

		try {
			iterator = project.onlySelectedSequencesIterator(orderList);
			while (iterator.hasNext() && !isCanceled()) {
				final ILightSequence sequence = iterator.next();
				if (!validSequence(sequence)) {
					continue;
				}
				final String sequenceString = sequence.getSeqString();
				if (Utilities.isSequenceNucletide(sequenceString)) {
					setFinishMessage("PSORTb requires protein sequences.");
					return;
				}
				//				sequencesProcessedCount++;

				if (lengthCount + sequence.getSeqLength() < LENGTH_LIMIT && iterator.hasNext()) {
					packToFile(file, sequence);
					lengthCount += sequence.getSeqLength();
				} else {
					// Max size reached.
					packToFile(file, sequence);
					lengthCount += sequence.getSeqLength();

					// Send the sequence pack

					scParameters.put(PSParameters.P_FILE_NAME.key(), FileUtils.getFileName(file.getAbsolutePath()));
					RequestPack packToSend;
					try {
						packToSend = new RequestPack(SERVICE_NAME, scParameters.toJsonString(), Arrays.asList(file.getAbsolutePath()));
						service.sendRequestPack(packToSend);
						//						postJobMessage(sequencesProcessedCount + "/" + project.getSelectedSequencesCount() + " sequences processed...");
					} catch (final JSONException e1) {
						log.error("Could not create the parameters", e1);
						throw new IllegalStateException("Could not create the parameters. Algorithm will stop");
					}

					// Reinitialize variables for the next pack.
					try {
						if (file != null) {
							file.delete();
						}
						file = File.createTempFile("fasta", ".fasta");
					} catch (final IOException e) {
						log.error("Could not create next temporary file. Algorithm will stop.", e);
						throw new IllegalStateException("Could not create next temporary file. Algorithm will stop");
					}
					lengthCount = 0;
				}
			}
		} catch (final RuntimeException ie) {
			if (ie.getCause() instanceof InterruptedException) {
				log.warn("Interrupted Algorithm : ", ie);
				service.terminate();
				throw new InterruptedException();
			} else if (ie.getCause() instanceof IllegalStateException) {
				log.warn("Illegal state in ServiceCloud. : ", ie);
				throw new InterruptedException();
			}
		} finally {
			if (file != null) {
				file.delete();
			}
			service.waitForTermination();
			addModificationInfo(resultObject);
		}

	}

	@Override
	protected void onInterruptionOccured() {
		service.terminate();
	}

	private void packToFile(final File file, final ILightSequence sequence) {
		final String string = getPropperFasta(sequence);
		try {
			Files.write(file.toPath(), Arrays.asList(string), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (final IOException e) {
			log.error("Could not write into temporary file. Algorithm will stop.", e);
			throw new IllegalStateException("Could not write into temporary file. Algorithm will stop");
		}
	}

	private boolean validSequence(final ILightSequence sequence) {
		if (sequence.getSeqLength() <= 0) {
			return false;
		}
		return true;
	}

	private String getPropperFasta(final ILightSequence sequence) {
		final StringBuilder fasta = new StringBuilder();
		fasta.append(">" + sequence.getName() + "\n");
		fasta.append(sequence.getSeqString() + "\n");
		return fasta.toString();
	}

}

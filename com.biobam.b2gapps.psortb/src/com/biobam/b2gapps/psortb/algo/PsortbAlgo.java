package com.biobam.b2gapps.psortb.algo;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;

import es.blast2go.data.IProject;


public class PsortbAlgo extends B2GJob<PsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(PsortbAlgo.class);

	public PsortbAlgo() {
		super("PSORTb", new PsortbParameters());
	}

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

	private int unknownCount = 0;
	private PsortbSCPackCreatorIteratorImpl packCreatorIterator;

	@Override
	public void run() throws InterruptedException {

		// Get Project and Order list.
		final IProject project = getInput(PsortbJobMetadata.INPUT_PROJECT);
		final ItemsOrderList orderList = getInput(PsortbJobMetadata.ADDITIONAL_ORDER_LIST);
		final PsortbParameters parameters = getParameters();

		// Check project preconditions.
		if (project.getSelectedSequencesCount() == 0) {
			setFinishMessage("No sequences selected.");
			return;
		}
		if (!project.atLeastOneSequenceComplies(SeqCondImpl.COND_HAS_SEQ_STRING)) {
			setFinishMessage("There are no sequences with sequence information, please load the original fasta-file and try again.");
			return;
		}

		beginTask(getName(), project.getSelectedSequencesCount());

		// Create the output PsortB adding empty entries.
		final Map<String, PsortbEntry> parseResult = new LinkedHashMap<String, PsortbEntry>();
		Iterator<ILightSequence> iterator = project.onlySelectedSequencesIterator(orderList);
		while (iterator.hasNext() && !isCanceled()) {
			final ILightSequence seq = iterator.next();
			parseResult.put(seq.getName(), PsortbEntry.createEmpty(seq.getName()));
		}

		final PsortbObject resultObject = PsortbObject.newInstance("PSORTb Results" + addInputObjectNames(), parseResult);
		addModificationInfo(resultObject);

		// Post the output object. Entries will be updated with results during the execution.
		postOutputResults(resultObject);

		try {
			// Create a cloud instance.
			IServiceCloud service = IServiceCloud.newInstance();

			// Define how the results files will be handled.
			service.setPackResultHandler(new IResultFSHandler() {
				int count = 0;

				@Override
				public void handleResult(final FileSystem fs) {
					final Path path = fs.getPath("output.txt");
					final Collection<PsortbEntry> entries = PsortbResultParser.parseResult(path, getIProgressMonitor());
					for (final PsortbEntry entry : entries) {
						resultObject.updateEntry(entry);

						String finalLocalization = entry.getFinalLocalization();
						if ("Unknown".equals(finalLocalization)) {
							unknownCount++;
						}
					}
					count += entries.size();
					postJobMessage(count + "/" + project.getSelectedSequencesCount() + " sequence results obtained...");
				}
			});

			// Create packs parameters using the helper class IServiceCloudParameters.
			final IServiceCloudParameters scParameters = IServiceCloudParameters.create();
			scParameters.put(PSParameters.P_ORGANISM.key(), parameters.organism.getValue()
			        .getId());
			scParameters.put(PSParameters.P_GRAM.key(), parameters.gram.getValue()
			        .getId());
			scParameters.put(PSParameters.P_ADVANCED_GRAM.key(), parameters.advancedGram.getValue()
			        .getId());
			scParameters.put(PSParameters.P_CUTOFF.key(), String.valueOf(parameters.cutoff.getValue() / 10));

			// Define how the packs to be sent are created by creating and setting an implementation of the PackCreatorIterator.
			packCreatorIterator = new PsortbSCPackCreatorIteratorImpl(project, orderList, scParameters);
			service.setPackCreatorIterator(packCreatorIterator);

			// Start sending. This will internally send packages available in the pack creator iterator and send them to be handled by the result handler.
			service.startSending();

		} finally {

			// Set the finish message (including process statistics).

			StringBuilder sb = new StringBuilder();

			if (isCanceled()) {
				sb.append("Algorithm execution cancelled.");
			} else {
				sb.append("PSORTb finished.");
			}

			if (packCreatorIterator == null || packCreatorIterator.getNucleotideSequencesCount() == 0) {
				sb.append("\nYou can merge the new GOs with the original project annotation.");
			} else {
				sb.append("\nPsortB can only process protein sequences.\n");
				sb.append(packCreatorIterator.getNucleotideSequencesCount() + " nucleotide sequences were skipped.");
			}

			sb.append("\nSequences with unknown location: " + unknownCount);
		}

	}

}

package com.biobam.b2gapps.psortb.algo;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;

import org.bioinfo.commons.io.utils.FileUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.algo.PsortbAlgo.PSParameters;
import com.biobam.blast2go.api.job.input.ItemsOrderList;
import com.biobam.blast2go.api.scm.ISCPackCreatorIterator;
import com.biobam.blast2go.api.scm.IServiceCloudParameters;
import com.biobam.blast2go.api.scm.RequestPack;
import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;

import es.blast2go.data.IProject;

final class PsortbSCPackCreatorIteratorImpl extends ISCPackCreatorIterator {
	private static final Logger log = LoggerFactory.getLogger(PsortbSCPackCreatorIteratorImpl.class);

	// PACK CREATOR CONSTANTS

	// We limit the sum of all amino acids to a bit more than this number.
	final int LENGTH_LIMIT = 2000;

	// INPUT DATA
	private final IProject project;
	private final ItemsOrderList orderList;

	// LOCAL VARIABLES
	Iterator<ILightSequence> projectIterator;
	private int nucleotideSequencesCount = 0;

	private IServiceCloudParameters scParameters;

	private String fastaFilePath;

	public PsortbSCPackCreatorIteratorImpl(IProject project, ItemsOrderList orderList, IServiceCloudParameters scParameters) {
		this.project = project;
		this.orderList = orderList;
		this.scParameters = scParameters;
		projectIterator = project.onlySelectedSequencesIterator(orderList);
	}

	private boolean validSequence(final ILightSequence sequence) {
		if (sequence.getSeqLength() <= 0) {
			return false;
		}
		return true;
	}

	private void addToFastaFile(final File file, final ILightSequence sequence) {
		final String string = getPropperFasta(sequence);
		try {
			Files.write(file.toPath(), Arrays.asList(string), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (final ClosedByInterruptException closed) {
			log.warn("Interrupted while writing into temporary file. Probably stopped Job.");
			throw new RuntimeException(closed);
		} catch (final IOException e) {
			log.error("Could not write into temporary file. Algorithm will stop.", e);
			throw new IllegalStateException("Could not write into temporary file. Algorithm will stop");
		}
	}

	private String getPropperFasta(final ILightSequence sequence) {
		final StringBuilder fasta = new StringBuilder();
		fasta.append(">" + sequence.getName() + "\n");
		fasta.append(sequence.getSeqString() + "\n");
		return fasta.toString();
	}

	@Override
	public boolean hasNext() {
		return projectIterator.hasNext();
	}

	@Override
	public RequestPack next() {
		int packageLength = 0;
		File file;

		// Create the FASTA file to be sent.
		try {
			file = File.createTempFile("fasta", ".fasta");
		} catch (final IOException e) {
			log.error("Could not create temporary file. Algorithm will stop.", e);
			throw new IllegalStateException("Could not create temporary file. Algorithm will stop");
		}

		while (projectIterator.hasNext() && packageLength < LENGTH_LIMIT) {
			final ILightSequence b2gSequence = projectIterator.next();
			if (!validSequence(b2gSequence)) {
				continue;
			}
			final String aminoacidsString = b2gSequence.getSeqString();
			if (Utilities.isSequenceNucletide(aminoacidsString)) {
				nucleotideSequencesCount++;
			} else {
				addToFastaFile(file, b2gSequence);
				packageLength += b2gSequence.getSeqLength();
			}
		}

		// After the file has been created, add the file name to parameters
		scParameters.put(PSParameters.P_FILE_NAME.key(), FileUtils.getFileName(file.getAbsolutePath()));

		// Finally create the actual Request Pack
		RequestPack pack;
		try {
			fastaFilePath = file.getAbsolutePath();
			pack = new RequestPack("psortb", "1", scParameters.toJsonString(), Arrays.asList(fastaFilePath));
		} catch (final JSONException e1) {
			log.error("Could not create the parameters", e1);
			throw new IllegalStateException("Could not create the parameters. Algorithm will stop");
		}

		return pack;
	}

	@Override
	public void remove() {
		// Remove the FASTA file.
		if (fastaFilePath != null) {
			new File(fastaFilePath).delete();
		}
	}

	public int getNucleotideSequencesCount() {
		return nucleotideSequencesCount;
	}
}
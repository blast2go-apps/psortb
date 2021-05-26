package com.biobam.b2gapps.psortb.algo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;

import org.bioinfo.commons.io.utils.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.blast2go.api.job.input.ItemsOrderList;
import com.biobam.blast2go.api.scm.ISCPackCreatorIterator;
import com.biobam.blast2go.api.scm.IServiceCloudParameters;
import com.biobam.blast2go.api.scm.RequestPack;
import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;

import es.blast2go.data.IProject;

final class PsortbPackageCreator extends ISCPackCreatorIterator {
	private static final Logger log = LoggerFactory.getLogger(PsortbPackageCreator.class);
	// We limit the sum of all amino acids to a bit more than this number.
	final int LENGTH_LIMIT = 4000;
	private Iterator<ILightSequence> projectIterator;
	private int nucleotideSequenceCount = 0;
	private int aminoAcidSequenceCount = 0;
	private IServiceCloudParameters scParameters;
	private IProgressMonitor monitor;
	private File tempFile;

	public PsortbPackageCreator(IProject project, ItemsOrderList orderList, IServiceCloudParameters scParameters, IProgressMonitor monitor) {
		this.scParameters = scParameters;
		this.monitor = monitor;
		projectIterator = project.onlySelectedSequencesIterator(orderList);
	}

	private boolean validSequence(final ILightSequence sequence) {
		if (sequence.getSeqLength() <= 0) {
			return false;
		}
		return true;
	}

	private void addToFastaFile(final File file, final ILightSequence sequence) {
		final String string = getProperFasta(sequence);
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

	private String getProperFasta(final ILightSequence sequence) {
		final StringBuilder fasta = new StringBuilder();
		fasta.append(">" + sequence.getName() + "\n");
		fasta.append(sequence.getSeqString());
		return fasta.toString();
	}

	@Override
	public boolean hasNext() {
		return projectIterator.hasNext();
	}

	@Override
	public RequestPack next() {
		int packageLength = 0;
		try {
			tempFile = File.createTempFile("psortb_", ".fasta");
			try (BufferedWriter bw = Utilities.bufferedWriter(tempFile.toPath(), false)) {
				while (projectIterator.hasNext() && packageLength < LENGTH_LIMIT) {
					ILightSequence sequence = projectIterator.next();
					if (!validSequence(sequence)) {
						continue;
					}
					String sequenceString = sequence.getSeqString();
					if (Utilities.isSequenceNucletide(sequenceString)) {
						nucleotideSequenceCount++;
					} else {
						aminoAcidSequenceCount++;
						bw.write(String.format(">%s\n%s\n", sequence.getName(), sequenceString));
						packageLength += sequenceString.length();
					}
					monitor.worked(1);
				}
				scParameters.put(PSParameters.P_FILE_NAME.key(), FileUtils.getFileName(tempFile.getAbsolutePath()));
				RequestPack pack = new RequestPack("psortb", "1", scParameters.toJsonString(), Arrays.asList(tempFile.getAbsolutePath()));
				return pack;
			}
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
	}

	@Override
	public void remove() {
		if (tempFile != null) {
			tempFile.delete();
		}
	}

	public int getNucleotideSequenceCount() {
		return nucleotideSequenceCount;
	}

	public int getAminoAcidSequenceCount() {
		return aminoAcidSequenceCount;
	}
}
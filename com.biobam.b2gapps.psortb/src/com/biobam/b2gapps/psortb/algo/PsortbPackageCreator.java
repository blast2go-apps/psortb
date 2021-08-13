package com.biobam.b2gapps.psortb.algo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.LineIterator;
import org.bioinfo.commons.io.utils.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.blast2go.api.scm.ISCPackCreatorIterator;
import com.biobam.blast2go.api.scm.IServiceCloudParameters;
import com.biobam.blast2go.api.scm.RequestPack;
import com.biobam.blast2go.basic_utilities.Utilities;

final class PsortbPackageCreator extends ISCPackCreatorIterator {
	private static final Logger log = LoggerFactory.getLogger(PsortbPackageCreator.class);
	// We limit the sum of all amino acids to this number and surplus
	final int LENGTH_LIMIT = 4000;
	private final IServiceCloudParameters scParameters;
	private final IProgressMonitor monitor;
	private final LineIterator iterator;
	private File tempFile;
	private String name = "";

	public PsortbPackageCreator(LineIterator iterator, IServiceCloudParameters scParameters, IProgressMonitor monitor) {
		this.iterator = iterator;
		this.scParameters = scParameters;
		this.monitor = monitor;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public RequestPack next() {
		int packageLength = 0;
		try {
			tempFile = File.createTempFile("psortb_", ".fasta");
			try (BufferedWriter bw = Utilities.bufferedWriter(tempFile.toPath(), false)) {
				while (iterator.hasNext()) {
					String line = iterator.next();
					int indexOf = line.indexOf(' ');
					if (indexOf > 1) {
						line = line.substring(0, indexOf);
					}
					if (line.charAt(0) == '>') {
						if (packageLength > LENGTH_LIMIT) {
							name = line;
							break;
						}
						bw.write(line);
						bw.write("\n");
					} else {
						if (!name.isBlank()) {
							bw.write(name);
							bw.write("\n");
							name = "";
						}
						bw.write(line);
						bw.write("\n");
						packageLength += line.length();
					}
					monitor.worked(1);
				}
			}
			scParameters.put(PSParameters.P_FILE_NAME.key(), FileUtils.getFileName(tempFile.getAbsolutePath()));
			return new RequestPack("psortb", "1", scParameters.toJsonString(), Arrays.asList(tempFile.getAbsolutePath()));
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
}
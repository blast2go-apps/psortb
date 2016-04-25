package com.biobam.b2gapps.psortb.algo.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.ITableTag;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.TableTag;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.TagColor;

public class PsortbResultParser {

	private PsortbResultParser() {
		super();
	}

	private static final Logger log = LoggerFactory.getLogger(PsortbResultParser.class);

	public static final Map<String, String> LOCATION_TO_GOID_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("Cytoplasmic", "GO:0005737");
			put("CytoplasmicMembrane", "GO:0009276");
			put("Periplasmic", "GO:0042597");
			put("OuterMembrane", "GO:0019867");
			put("ExtracellularSpace", "GO:0005615");
			put("Extracellular", "GO:0005576");
			put("CellWall", "GO:0005618");
		}
	});

	public static final Map<String, ITableTag> LOCATION_TO_TAG_MAP = Collections.unmodifiableMap(new HashMap<String, ITableTag>() {
		private static final long serialVersionUID = 1L;

		{
			put("Cytoplasmic", TableTag.create("Cytoplasmic", TagColor.ORANGE));
			put("CytoplasmicMembrane", TableTag.create("CytoplasmicMembrane", TagColor.RED));
			put("Periplasmic", TableTag.create("Periplasmic", TagColor.GREEN));
			put("OuterMembrane", TableTag.create("OuterMembrane", TagColor.YELLOW));
			put("ExtracellularSpace", TableTag.create("ExtracellularSpace", TagColor.PINK));
			put("Extracellular", TableTag.create("Extracellular", TagColor.BLUE));
			put("CellWall", TableTag.create("CellWall", TagColor.PURPLE));
		}
	});

	public static PsortbEntry parseLine(final String line) {
		if (line == null) {
			throw new NullPointerException("Line can not be null");
		}
		final String[] lineSplit = line.split("\t");
		if (lineSplit.length > 2) {
			final String name = lineSplit[0].trim();
			final String location = lineSplit[1].trim();
			final double score = Double.parseDouble(lineSplit[2].trim());
			return PsortbEntry.create(name, location, score);
		}
		log.error("Wrong file format. Line: {}", line);
		throw new IllegalStateException("Wrong file format");
	}

	public static Collection<PsortbEntry> parseResult(final Path path, final IProgressMonitor monitor) {
		final List<PsortbEntry> entries = new ArrayList<PsortbEntry>();
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			// Skip first line
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				entries.add(parseLine(line));
				//				System.out.println(line);
				monitor.worked(1);
			}
		} catch (final IOException e) {
			log.error("", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					log.error("", e);
				}
			}
		}
		return entries;
	}

}

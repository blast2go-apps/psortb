package com.biobam.b2gapps.psortb.algo.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbEntry.Builder;

public class PsortbResultParser {
	private static final Logger log = LoggerFactory.getLogger(PsortbResultParser.class);

	private static final Map<String, String> LOCATION_TO_GOID_MAP = new HashMap<String, String>();
	static {
		LOCATION_TO_GOID_MAP.put("cytoplasmic", "GO:0005737");
		LOCATION_TO_GOID_MAP.put("cytoplasmicmembrane", "GO:0009276");
		LOCATION_TO_GOID_MAP.put("periplasmic", "GO:0042597");
		LOCATION_TO_GOID_MAP.put("outermembrane", "GO:0019867");
		LOCATION_TO_GOID_MAP.put("extracellularspace", "GO:0005615");
		LOCATION_TO_GOID_MAP.put("extracellular", "GO:0005576");
		LOCATION_TO_GOID_MAP.put("cellwall", "GO:0005618");
	}

	public static boolean containsLocationName(String locationName) {
		if (locationName == null) {
			return false;
		}
		return LOCATION_TO_GOID_MAP.containsKey(locationName.toLowerCase());
	}

	public static String getGoId(String locationName) {
		return LOCATION_TO_GOID_MAP.get(locationName.toLowerCase());
	}

	public static Collection<PsortbEntry> parseResult(final Path path, final IProgressMonitor monitor) {
		final List<PsortbEntry> entries = new ArrayList<PsortbEntry>();
		try {
			BufferedReader in = Files.newBufferedReader(path);
			Iterable<CSVRecord> records;
			records = CSVFormat.TDF.withHeader()
			        .parse(in);
			for (CSVRecord record : records) {
				String sequenceName = record.get("SeqID");
				Builder eb = new PsortbEntry.Builder(sequenceName);

				String finalLocalizationHeader = "Final_Localization";
				if (record.isMapped(finalLocalizationHeader)) {
					eb.setFinalLocalization(record.get(finalLocalizationHeader));
				}

				String finalScoreHeader = "Final_Score";
				if (record.isMapped(finalScoreHeader)) {
					eb.setFinalScore(Double.parseDouble(record.get(finalScoreHeader)));
				}

				String secondaryLocalizationHeader = "Secondary_Localization";
				if (record.isMapped(secondaryLocalizationHeader)) {
					eb.setSecondaryLocalization(record.get(secondaryLocalizationHeader));
				}

				String cytoplasmicScoreHeader = "Cytoplasmic_Score";
				if (record.isMapped(cytoplasmicScoreHeader)) {
					eb.setCytoplasmicScore(Double.parseDouble(record.get(cytoplasmicScoreHeader)));
				}

				String cytoplasmicMembraneScoreHeader = "CytoplasmicMembrane_Score";
				if (record.isMapped(cytoplasmicMembraneScoreHeader)) {
					eb.setCytoplasmicMembraneScore(Double.parseDouble(record.get(cytoplasmicMembraneScoreHeader)));
				}

				String cellwallScoreHeader = "Cellwall_Score";
				if (record.isMapped(cellwallScoreHeader)) {
					eb.setCellwallScore(Double.parseDouble(record.get(cellwallScoreHeader)));
				}

				String extracellularScoreHeader = "Extracellular_Score";
				if (record.isMapped(extracellularScoreHeader)) {
					eb.setExtracellularScore(Double.parseDouble(record.get(extracellularScoreHeader)));
				}

				String periplasmicScoreHeader = "Periplasmic_Score";
				if (record.isMapped(periplasmicScoreHeader)) {
					eb.setPeriplasmicScore(Double.parseDouble(record.get(periplasmicScoreHeader)));
				}

				String outerMembraneScore = "OuterMembrane_Score";
				if (record.isMapped(outerMembraneScore)) {
					eb.setOuterMembraneScore(Double.parseDouble(record.get(outerMembraneScore)));
				}
				entries.add(eb.build());
				monitor.worked(1);
			}
		} catch (IOException e) {
			log.error("", e);
		}
		return entries;
	}
}

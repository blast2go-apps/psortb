package com.biobam.b2gapps.psortb.algo.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbEntry.Builder;
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

package com.biobam.b2gapps.psortb.algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.B2GJob;
import com.biobam.blast2go.api.scm.IResultFSHandler;
import com.biobam.blast2go.api.scm.IServiceCloud;
import com.biobam.blast2go.api.scm.IServiceCloudParameters;
import com.biobam.blast2go.basic_utilities.Utilities;
import com.biobam.omicsbox.webcharts.WebChart;
import com.biobam.omicsbox.webcharts.WebChartGenerator;
import com.biobam.omicsbox.webcharts.WebChartUtils.AXIS_TYPE;
import com.biobam.omicsbox.webcharts.WebChartUtils.SIDEBAR_MODULES;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class PsortbJob extends B2GJob<PsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(PsortbJob.class);
	private WebChart webChart;

	public PsortbJob() {
		super("PSORTb", new PsortbParameters());
	}

	@Override
	public void run() throws InterruptedException {
		Multiset<String> locations = HashMultiset.create();
		PsortbParameters parameters = getParameters();
		int totalSequences = 0;
		try (BufferedReader br = Utilities.bufferedReader(Paths.get(parameters.inputFasta.getValue()))) {
			String line = "";
			while (null != (line = br.readLine())) {
				if (line.charAt(0) == '>') {
					totalSequences++;
				} else if (line.length() > 10 && Utilities.isSequenceNucletide(line)) {
					terminateWithError("Psortb only works with protein sequences.\n" + line );
				}
			}
		} catch (IOException e) {
			terminateWithError("Problem reading " + Utilities.filename(parameters.inputFasta.getValue()), e);
		}

		beginTask(getName(), totalSequences * 2);
		Map<String, PsortbEntry> parseResult = new LinkedHashMap<>();
		PsortbObject resultObject = PsortbObject.newInstance("PSORTb Results" + addInputObjectNames(), parseResult);
		IServiceCloudParameters scParameters = IServiceCloudParameters.create();
		scParameters.put(PSParameters.P_ORGANISM.key(), parameters.organism.getValue()
		        .getId());
		scParameters.put(PSParameters.P_GRAM.key(), parameters.gram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_ADVANCED_GRAM.key(), parameters.advancedGram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_CUTOFF.key(), String.valueOf(parameters.cutoff.getValue() / 10));
		File inputFasta = new File(parameters.inputFasta.getValue());
		try (BufferedReader br = Utilities.bufferedReader(inputFasta.toPath())) {
			LineIterator iterator = new LineIterator(br);
			PsortbPackageCreator packCreator = new PsortbPackageCreator(iterator, scParameters, getIProgressMonitor());
			IServiceCloud service = IServiceCloud.newInstance();
			service.setPackResultHandler(resultHandler(locations, resultObject));
			service.setPackCreatorIterator(packCreator);
			service.startSending();
			Map<String, Integer> data = new HashMap<>();
			for (String location : locations.elementSet()) {
				data.put(location, locations.count(location));
			}
			if (!resultObject.getIdList()
			        .isEmpty()) {
				addModificationInfo(resultObject);
				postOutput(PsortbMetadata.PSORTB_RESULTS, resultObject);
				webChart = WebChartGenerator.create(data)
				        .createPieChart()
				        .autoCategoryColors()
				        .setChartTitle("Predicted Locations")
				        .setAxisLabels("Location", "Count")
				        .setXAxisSplitLine(true)
				        .enableSorting(true)
				        .setAxisTypes(AXIS_TYPE.CATEGORY, AXIS_TYPE.NUMERIC)
				        .addSidebarModules(SIDEBAR_MODULES.DEFAULT_FORMATTING, SIDEBAR_MODULES.SORTING_OPTIONS, SIDEBAR_MODULES.PLOT_EDITOR_BARS, SIDEBAR_MODULES.PLOT_EDITOR_PIE)
				        .build();
			}
			//			if (packCreator.getAminoAcidSequenceCount() == 0) {
			//				String msg = "PSORTb failed because no amino acid sequences could be found.";
			//				setFinishStatus(ERROR_STATUS);
			//				setFinishMessage(msg);
			//			} else if (packCreator.getNucleotideSequenceCount() == 0) {
			if (!resultObject.getIdList()
			        .isEmpty()) {
				postOutput(PsortbMetadata.CHART, webChart);
			}
			String msg = "PSORTb finished, the linked GOs can now be merged with the sequence project annotation.";
			setFinishMessage(msg);
			//			} else {
			//				if (!resultObject.getIdList()
			//				        .isEmpty()) {
			//					postOutput(PsortbMetadata.CHART, webChart);
			//				}
			//				StringBuilder sb = new StringBuilder();
			//				sb.append("PSORTb can only process amino acid sequences.\n");
			//				sb.append(packCreator.getNucleotideSequenceCount() + " nucleotide sequences were skipped.");
			//				setFinishStatus(WARNING_STATUS);
			//				setFinishMessage(sb.toString());
			//			}
		} catch (IOException e) {
			terminateWithError("Problem reading " + Utilities.filename(parameters.inputFasta.getValue()), e);
		}
	}

	public IResultFSHandler resultHandler(Multiset<String> locations, PsortbObject resultObject) {
		return new IResultFSHandler() {

			@Override
			public void handleResult(final FileSystem fs) {
				Path path = fs.getPath("output.txt");
				Collection<PsortbEntry> entries = PsortbResultParser.parseResult(path, getIProgressMonitor());
				for (PsortbEntry entry : entries) {
					resultObject.add(entry);
					String finalLocalization = entry.getFinalLocalization();
					locations.add(finalLocalization);
				}
				worked(entries.size());
			}
		};
	}
}

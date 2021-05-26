package com.biobam.b2gapps.psortb.algo;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
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
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;
import com.biobam.omicsbox.webcharts.WebChart;
import com.biobam.omicsbox.webcharts.WebChartGenerator;
import com.biobam.omicsbox.webcharts.WebChartUtils.AXIS_TYPE;
import com.biobam.omicsbox.webcharts.WebChartUtils.SIDEBAR_MODULES;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import es.blast2go.data.IProject;

public class PsortbAlgo extends B2GJob<PsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(PsortbAlgo.class);
	private final Multiset<String> locations = HashMultiset.create();
	private WebChart webChart;

	public PsortbAlgo() {
		super("PSORTb", new PsortbParameters());
	}

	@Override
	public void run() throws InterruptedException {
		IProject project = getInput(PsortbJobMetadata.INPUT_PROJECT);
		ItemsOrderList orderList = getInput(PsortbJobMetadata.ADDITIONAL_ORDER_LIST);
		PsortbParameters parameters = getParameters();
		if (project.getSelectedSequencesCount() == 0) {
			terminateWithError("No sequences selected.");
		}
		if (!project.atLeastOneSequenceComplies(SeqCondImpl.COND_HAS_SEQ_STRING)) {
			terminateWithError("There are no sequences with sequence information, please load the original fasta-file and try again.");
		}
		beginTask(getName(), project.getSelectedSequencesCount() * 2);
		Map<String, PsortbEntry> parseResult = new LinkedHashMap<String, PsortbEntry>();
		PsortbObject resultObject = PsortbObject.newInstance("PSORTb Results" + addInputObjectNames(), parseResult);
		IServiceCloudParameters scParameters = IServiceCloudParameters.create();
		scParameters.put(PSParameters.P_ORGANISM.key(), parameters.organism.getValue()
		        .getId());
		scParameters.put(PSParameters.P_GRAM.key(), parameters.gram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_ADVANCED_GRAM.key(), parameters.advancedGram.getValue()
		        .getId());
		scParameters.put(PSParameters.P_CUTOFF.key(), String.valueOf(parameters.cutoff.getValue() / 10));
		PsortbPackageCreator packCreator = new PsortbPackageCreator(project, orderList, scParameters, getIProgressMonitor());
		try {
			// Create a cloud instance.
			IServiceCloud service = IServiceCloud.newInstance();

			// Define how the results files will be handled.
			service.setPackResultHandler(new IResultFSHandler() {
				int count = 0;

				@Override
				public void handleResult(final FileSystem fs) {
					Path path = fs.getPath("output.txt");
					Collection<PsortbEntry> entries = PsortbResultParser.parseResult(path, getIProgressMonitor());
					for (PsortbEntry entry : entries) {
						resultObject.add(entry);
						String finalLocalization = entry.getFinalLocalization();
						locations.add(finalLocalization);
					}
					count += entries.size();
					postJobMessage(count + "/" + project.getSelectedSequencesCount() + " results obtained");
				}
			});
			service.setPackCreatorIterator(packCreator);
			service.startSending();
			Map<String, Integer> data = new HashMap<>();
			for (String location : locations.elementSet()) {
				data.put(location, locations.count(location));
			}
			if (!resultObject.getIdList()
			        .isEmpty()) {
				addModificationInfo(resultObject);
				postOutput(PsortbJobMetadata.PSORTB_RESULTS, resultObject);
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
		} finally {
			if (packCreator.getAminoAcidSequenceCount() == 0) {
				String msg = "PSORTb failed because no amino acid sequences could be found.";
				setFinishStatus(ERROR_STATUS);
				setFinishMessage(msg);
			} else if (packCreator.getNucleotideSequenceCount() == 0) {
				if (!resultObject.getIdList()
				        .isEmpty()) {
					postOutput(PsortbJobMetadata.CHART, webChart);
				}
				String msg = "PSORTb finished, the linked GOs can now be merged with the sequence project annotation.";
				setFinishMessage(msg);
			} else {
				if (!resultObject.getIdList()
				        .isEmpty()) {
					postOutput(PsortbJobMetadata.CHART, webChart);
				}
				StringBuilder sb = new StringBuilder();
				sb.append("PSORTb can only process amino acid sequences.\n");
				sb.append(packCreator.getNucleotideSequenceCount() + " nucleotide sequences were skipped.");
				setFinishStatus(WARNING_STATUS);
				setFinishMessage(sb.toString());
			}
		}
	}
}

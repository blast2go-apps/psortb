package com.biobam.b2gapps.psortb.algo;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
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
import com.biobam.omicsbox.webcharts.WebChart;
import com.biobam.omicsbox.webcharts.WebChartGenerator;
import com.biobam.omicsbox.webcharts.WebChartUtils.AXIS_TYPE;
import com.biobam.omicsbox.webcharts.WebChartUtils.SIDEBAR_MODULES;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import es.blast2go.data.IProject;

public class PsortbAlgo extends B2GJob<PsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(PsortbAlgo.class);
	private PsortbSCPackCreatorIteratorImpl packCreatorIterator;
	private final Multiset<String> locations = HashMultiset.create();

	public PsortbAlgo() {
		super("PSORTb", new PsortbParameters());
	}

	@Override
	public void run() throws InterruptedException {
		final IProject project = getInput(PsortbJobMetadata.INPUT_PROJECT);
		final ItemsOrderList orderList = getInput(PsortbJobMetadata.ADDITIONAL_ORDER_LIST);
		final PsortbParameters parameters = getParameters();

		if (project.getSelectedSequencesCount() == 0) {
			terminateWithError("No sequences selected.");
		}
		if (!project.atLeastOneSequenceComplies(SeqCondImpl.COND_HAS_SEQ_STRING)) {
			terminateWithError("There are no sequences with sequence information, please load the original fasta-file and try again.");
		}

		beginTask(getName(), project.getSelectedSequencesCount() * 2);

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
		postOutput(PsortbJobMetadata.PSORTB_RESULTS, resultObject);

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
						resultObject.updateEntry(entry);
						String finalLocalization = entry.getFinalLocalization();
						locations.add(finalLocalization);
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
			packCreatorIterator = new PsortbSCPackCreatorIteratorImpl(project, orderList, scParameters, getIProgressMonitor());
			service.setPackCreatorIterator(packCreatorIterator);

			// Start sending. This will internally send packages available in the pack creator iterator and send them to be handled by the result handler.
			service.startSending();

			Map<String, Integer> data = new HashMap<>();
			for (String location : locations.elementSet()) {
				data.put(location, locations.count(location));
			}
			WebChart webChart = WebChartGenerator.create(data)
			        .createPieChart()
			        .autoCategoryColors()
			        .setChartTitle("Predicted Locations")
			        .setAxisLabels("Location", "Count")
			        .setXAxisSplitLine(true)
			        .enableSorting(true)
			        .setAxisTypes(AXIS_TYPE.CATEGORY, AXIS_TYPE.NUMERIC)
			        .addSidebarModules(SIDEBAR_MODULES.DEFAULT_FORMATTING, SIDEBAR_MODULES.SORTING_OPTIONS, SIDEBAR_MODULES.PLOT_EDITOR_BARS, SIDEBAR_MODULES.PLOT_EDITOR_PIE)
			        .build();
			postOutput(PsortbJobMetadata.WEB_CHART, webChart);

		} finally {
			StringBuilder sb = new StringBuilder();
			if (isCanceled()) {
				sb.append("Algorithm execution cancelled.");
			} else {
				sb.append("PSORTb finished.");
			}
			if (packCreatorIterator == null || packCreatorIterator.getNucleotideSequencesCount() == 0) {
				sb.append("\nYou can merge the new GOs with the original project annotation.");
			} else {
				if (packCreatorIterator.getNucleotideSequencesCount() == project.size()) {
					setFinishStatus(ERROR_STATUS);
				} else {
					setFinishStatus(WARNING_STATUS);
				}
				sb.append("\nPsortB can only process protein sequences.\n");
				sb.append(packCreatorIterator.getNucleotideSequencesCount() + " nucleotide sequences were skipped.");
			}
			sb.append("\nSequences with unknown location: " + locations.count("Unknown"));
		}
	}
}

package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.blast2go.api.job.input.ItemsOrderList;
import com.biobam.omicsbox.webcharts.WebChart;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class PsortbJobMetadata implements IB2GJobMetadata<PsortbAlgo, PsortbParameters> {
	public static final InputDefinition<IProject> INPUT_PROJECT = IProjectConstants.INPUT_DEFINITION;
	public static final InputDefinition<ItemsOrderList> ADDITIONAL_ORDER_LIST = ItemsOrderList.INPUT_DEFINITION_OPTIONAL;
	public static final InputDefinition<PsortbObject> PSORTB_RESULTS = InputDefinition.create(PsortbObject.class, "psortb_result", "PSORTb result");
	public static final InputDefinition<WebChart> WEB_CHART = new InputDefinition<>(WebChart.class, "psortb_chart", "Result Summary");

	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.listOf(INPUT_PROJECT);
	}

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.listOf(ADDITIONAL_ORDER_LIST);
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.listOf(PSORTB_RESULTS, WEB_CHART);
	}

	@Override
	public Class<PsortbAlgo> jobClass() {
		return PsortbAlgo.class;
	}

	@Override
	public Class<PsortbParameters> parametersClass() {
		return PsortbParameters.class;
	}

}

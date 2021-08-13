package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.omicsbox.webcharts.WebChart;

public class PsortbMetadata implements IB2GJobMetadata<PsortbJob, PsortbParameters> {
	public static final InputDefinition<PsortbObject> PSORTB_RESULTS = InputDefinition.create(PsortbObject.class, "psortb_result", "PSORTb result");
	public static final InputDefinition<WebChart> CHART = new InputDefinition<>(WebChart.class, "psortb_chart", "Result Summary");

	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.listOf(PSORTB_RESULTS, CHART);
	}

	@Override
	public Class<PsortbJob> jobClass() {
		return PsortbJob.class;
	}

	@Override
	public Class<PsortbParameters> parametersClass() {
		return PsortbParameters.class;
	}
}

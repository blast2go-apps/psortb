package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.omicsbox.webcharts.WebChart;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class MergePsortbJobMetadata implements IB2GJobMetadata<MergePsortbAlgo, MergePsortbParameters> {
	public static final InputDefinition<IProject> PROJECT = IProjectConstants.INPUT_DEFINITION;
	public static final InputDefinition<PsortbObject> INPUT_PSORTB_RESULT = InputDefinition.create(PsortbObject.class, "PSORTb Result", "PSORTb Result");
	
	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.listOf(INPUT_PSORTB_RESULT);
	}

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.listOf(PROJECT);
	}

	@Override
	public Class<MergePsortbAlgo> jobClass() {
		return MergePsortbAlgo.class;
	}

	@Override
	public Class<MergePsortbParameters> parametersClass() {
		return MergePsortbParameters.class;
	}
}

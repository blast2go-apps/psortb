package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.blast2go.api.job.parameters.EmptyParameters;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class MergePsortbWorkflowJobMetadata implements IB2GJobMetadata<MergePsortbWorkflowAlgo, EmptyParameters> {
	public static final InputDefinition<IProject> INPUT_PROJECT = IProjectConstants.INPUT_DEFINITION;
	public static final InputDefinition<PsortbObject> INPUT_PSORTB_RESULT = InputDefinition.create(PsortbObject.class, "psortb_result", "PSORTb Result");

	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.listOf(INPUT_PROJECT, INPUT_PSORTB_RESULT);
	}

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.listOf(INPUT_PROJECT);
	}

	@Override
	public Class<MergePsortbWorkflowAlgo> jobClass() {
		return MergePsortbWorkflowAlgo.class;
	}

	@Override
	public Class<EmptyParameters> parametersClass() {
		return EmptyParameters.class;
	}
}

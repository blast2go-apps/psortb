package com.biobam.b2gapps.psortb.merge;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.blast2go.api.job.parameters.EmptyParameters;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class MergePsortbWorkflowMetadata implements IB2GJobMetadata<MergePsortbWorkflowJob, EmptyParameters> {
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
	public Class<MergePsortbWorkflowJob> jobClass() {
		return MergePsortbWorkflowJob.class;
	}

	@Override
	public Class<EmptyParameters> parametersClass() {
		return EmptyParameters.class;
	}
}

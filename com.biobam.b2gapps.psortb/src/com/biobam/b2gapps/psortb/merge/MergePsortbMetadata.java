package com.biobam.b2gapps.psortb.merge;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class MergePsortbMetadata implements IB2GJobMetadata<MergePsortbJob, MergePsortbParameters> {
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
	public Class<MergePsortbJob> jobClass() {
		return MergePsortbJob.class;
	}

	@Override
	public Class<MergePsortbParameters> parametersClass() {
		return MergePsortbParameters.class;
	}
}

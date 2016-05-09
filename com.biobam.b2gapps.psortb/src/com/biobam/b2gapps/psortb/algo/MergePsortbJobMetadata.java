package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class MergePsortbJobMetadata implements
		IB2GJobMetadata<MergePsortbAlgo, MergePsortbParameters> {

	public static final InputDefinition<IProject> INPUT_PROJECT = IProjectConstants.INPUT_DEFINITION;

	@Override
	public List<InputDefinition<?>> inputs() {
		return InputDefinition.listOf(INPUT_PROJECT);
	}

	@Override
	public List<InputDefinition<?>> additionalRequirements() {
		return InputDefinition.EMPTY_LIST;
	}

	@Override
	public List<InputDefinition<?>> outputs() {
		return InputDefinition.EMPTY_LIST;
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

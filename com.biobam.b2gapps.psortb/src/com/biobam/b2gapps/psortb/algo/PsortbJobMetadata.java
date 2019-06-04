package com.biobam.b2gapps.psortb.algo;

import java.util.List;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.IB2GJobMetadata;
import com.biobam.blast2go.api.job.InputDefinition;
import com.biobam.blast2go.api.job.input.ItemsOrderList;

import es.blast2go.data.IProject;
import es.blast2go.data.IProjectConstants;

public class PsortbJobMetadata implements IB2GJobMetadata<PsortbAlgo, PsortbParameters> {

	public static InputDefinition<IProject> INPUT_PROJECT = IProjectConstants.INPUT_DEFINITION;
	public static InputDefinition<ItemsOrderList> ADDITIONAL_ORDER_LIST = ItemsOrderList.INPUT_DEFINITION_OPTIONAL;
	public static InputDefinition<PsortbObject> OUTPUT_RESULT = InputDefinition.create(PsortbObject.class, "psortb_result", "PSORTb result");

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
		return InputDefinition.listOf(OUTPUT_RESULT);
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

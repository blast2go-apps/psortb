package com.biobam.b2gapps.psortb.actions;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.b2gapps.psortb.algo.PsortbJobMetadata;
import com.biobam.b2gapps.psortb.wizards.PsortbWizard;
import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.internal.menugroups.FunctionalGenomicsGroup;
import com.biobam.blast2go.api.user.Feature;
import com.biobam.blast2go.api.wizard.B2GWizard;

public class RunPsortbAction extends B2GAction<PsortbJobMetadata> {

	@Override
	public IB2GBaseGroup getActionGroup() {
		return FunctionalGenomicsGroup.INSTANCE;
	}

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.TOOLBAR, PlaceTag.WORKFLOW);
	}

	@Override
	public String getName() {
		return "Run PSORTb";
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 19;
	}

	@Override
	public Class<? extends B2GWizard<?>> getWizardClass() {
		return PsortbWizard.class;
	}

	@Override
	public Class<PsortbJobMetadata> jobMetadataClass() {
		return PsortbJobMetadata.class;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.RUN;
	}

	@Override
	public String getId() {
		return "com.biobam.b2gapps.psortb.action";
	}

	@Override
	public EnumSet<Feature> executionPermissions() {
		return EnumSet.of(Feature.FUNCTIONAL_GENOMICS);
	}

}

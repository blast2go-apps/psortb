package com.biobam.b2gapps.psortb.actions;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.b2gapps.psortb.algo.PsortbJobMetadata;
import com.biobam.b2gapps.psortb.wizards.PsortbWizard;
import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.wizard.B2GWizard;

public class RunPsortbAction extends B2GAction<PsortbJobMetadata> {

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.WORKFLOW, PlaceTag.MENU_ANALYSIS);
	}

	@Override
	public String getName() {
		return "Run PSORTb (Online)";
	}

	@Override
	public IB2GIcon getActionIcon() {
		return IB2GIcon.DEFAULT_EMPTY_ICON;
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 0;
	}

	@Override
	public IB2GBaseGroup getActionGroup() {
		return PsortbGroup.INSTANCE;
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

}
package com.biobam.b2gapps.psortb.actions;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.b2gapps.psortb.algo.MergePsortbJobMetadata;
import com.biobam.b2gapps.psortb.wizards.MergePsortbWizard;
import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.wizard.B2GWizard;

public class MergePsortbAction extends B2GAction<MergePsortbJobMetadata> {

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.WORKFLOW, PlaceTag.MAIN_MENU);
	}

	@Override
	public String getName() {
		return "Merge PSORTb GOs to Annotation";
	}

	@Override
	public IB2GIcon getActionIcon() {
		return IB2GIcon.DEFAULT_EMPTY_ICON;
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 1;
	}

	@Override
	public IB2GBaseGroup getActionGroup() {
		return PsortbGroup.INSTANCE;
	}

	@Override
	public Class<? extends B2GWizard<?>> getWizardClass() {
		return MergePsortbWizard.class;
	}

	@Override
	public Class<MergePsortbJobMetadata> jobMetadataClass() {
		return MergePsortbJobMetadata.class;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.RUN;
	}

	@Override
	public String getId() {
		return "com.biobam.b2gapps.psortb.merge.action";
	}

}

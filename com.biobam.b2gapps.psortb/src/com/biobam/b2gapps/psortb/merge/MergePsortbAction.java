package com.biobam.b2gapps.psortb.merge;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.icon.DefaultB2GIcons;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.user.Feature;
import com.biobam.blast2go.api.wizard.B2GWizard;

public class MergePsortbAction extends B2GAction<MergePsortbMetadata> {

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.PART_SIDE_BAR);
	}

	@Override
	public String getName() {
		return "Merge GOs to Project";
	}

	@Override
	public IB2GIcon getActionIcon() {
		return DefaultB2GIcons.PROJECT_ICON;
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 1;
	}

	@Override
	public IB2GBaseGroup getActionGroup() {
		return IB2GBaseGroup.BASE_GROUP;
	}

	@Override
	public Class<? extends B2GWizard<?>> getWizardClass() {
		return MergePsortbWizard.class;
	}

	@Override
	public Class<MergePsortbMetadata> jobMetadataClass() {
		return MergePsortbMetadata.class;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.RUN;
	}

	@Override
	public String getId() {
		return "com.biobam.b2gapps.psortb.merge.action";
	}

	@Override
	public EnumSet<Feature> executionPermissions() {
		return EnumSet.of(Feature.FUNCTIONAL_GENOMICS);
	}

}

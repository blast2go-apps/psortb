package com.biobam.b2gapps.psortb.actions;

import java.util.EnumSet;
import java.util.Set;

import com.biobam.b2gapps.psortb.algo.MergePsortbWorkflowJobMetadata;
import com.biobam.blast2go.api.action.ActionType;
import com.biobam.blast2go.api.action.B2GAction;
import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.PlaceTag;
import com.biobam.blast2go.api.action.icon.DefaultB2GIcons;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.action.internal.menugroups.FunctionalGenomicsGroup;
import com.biobam.blast2go.api.user.Feature;
import com.biobam.blast2go.api.wizard.B2GWizard;

public class MergePsortbWorkflowAction extends B2GAction<MergePsortbWorkflowJobMetadata> {

	@Override
	public Set<PlaceTag> getPlaceTags() {
		return EnumSet.of(PlaceTag.WORKFLOW);
	}

	@Override
	public String getName() {
		return "Merge PSORTb GOs to Annotation";
	}

	@Override
	public IB2GIcon getActionIcon() {
		return DefaultB2GIcons.PROJECT_ICON;
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 20;
	}

	@Override
	public IB2GBaseGroup getActionGroup() {
		return FunctionalGenomicsGroup.INSTANCE;
	}

	@Override
	public Class<? extends B2GWizard<?>> getWizardClass() {
		return null;
	}

	@Override
	public Class<MergePsortbWorkflowJobMetadata> jobMetadataClass() {
		return MergePsortbWorkflowJobMetadata.class;
	}

	@Override
	public ActionType getActionType() {
		return ActionType.RUN;
	}

	@Override
	public String getId() {
		return "com.biobam.b2gapps.psortb.merge.workflow.action";
	}

	@Override
	public EnumSet<Feature> executionPermissions() {
		return EnumSet.noneOf(Feature.class);
	}

}

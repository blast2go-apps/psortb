package com.biobam.b2gapps.psortb.actions;

import java.util.Collections;
import java.util.List;

import com.biobam.blast2go.api.action.IB2GBaseGroup;
import com.biobam.blast2go.api.action.icon.B2GIconImpl;
import com.biobam.blast2go.api.action.icon.IB2GIcon;
import com.biobam.blast2go.api.action.internal.menugroups.AnalysisSuperActionGroup;

public class PsortbGroup implements IB2GBaseGroup {

	static final PsortbGroup INSTANCE = new PsortbGroup();

	/**
	 * Can't be instantiated outside the class <br>
	 * Use INSTANCE instead.
	 */
	private PsortbGroup() {}

	@Override
	public String getGroupId() {
		return "com.biobam.blast2go.psortb.group";
	}

	@Override
	public String getName() {
		return "PSORTb";
	}

	@Override
	public IB2GBaseGroup getParentGroup() {
		return AnalysisSuperActionGroup.INSTANCE;
	}

	@Override
	public IB2GIcon getGroupIcon() {
		return IB2GIcon.DEFAULT_EMPTY_ICON;
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 8;
	}

	@Override
	public List<Integer> getSeparatorsPrefferedPositions() {
		return Collections.emptyList();
	}

}

package com.biobam.b2gapps.psortb.viewer;

import java.util.EnumSet;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.dataviewer.interfaces.IB2GObjectController;
import com.biobam.blast2go.api.dataviewer.interfaces.IB2GObjectControllerCreator;
import com.biobam.blast2go.api.dataviewer.interfaces.WorkbenchProperty;

public class PsortbGenericTableControllerCreator implements IB2GObjectControllerCreator<PsortbObject> {

	@Override
	public Class<? extends IB2GObjectController<PsortbObject>> getObjectControllerClass() {
		return PsortbGenericTableController.class;
	}

	@Override
	public Class<PsortbObject> getObjectClass() {
		return PsortbObject.class;
	}

	@Override
	public IB2GObjectController<PsortbObject> getControllerInstance(PsortbObject object) {
		return new PsortbGenericTableController(object);
	}

	@Override
	public int getPreferredPositionInMenu() {
		return 0;
	}

	@Override
	public String getViewerName() {
		return "Table";
	}

	@Override
	public EnumSet<WorkbenchProperty> getWorkbenchProperties() {
		return EnumSet.of(WorkbenchProperty.DEFAULT_VIEWER, WorkbenchProperty.IMPORTANCE_PRIMARY_VIEWER, WorkbenchProperty.POSITION_MAIN, WorkbenchProperty.SAVE_ALLOWED, WorkbenchProperty.AUTOSAVE_ALLOWED, WorkbenchProperty.ENABLE_SIDE_BAR);
	}

}

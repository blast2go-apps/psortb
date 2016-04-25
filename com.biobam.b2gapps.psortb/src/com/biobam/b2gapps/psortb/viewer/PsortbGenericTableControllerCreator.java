package com.biobam.b2gapps.psortb.viewer;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.dataviewer.interfaces.IB2GObjectController;
import com.biobam.blast2go.api.dataviewer.interfaces.IB2GObjectControllerCreator;

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

}

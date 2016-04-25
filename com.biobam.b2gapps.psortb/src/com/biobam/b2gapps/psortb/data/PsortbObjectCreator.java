package com.biobam.b2gapps.psortb.data;

import java.net.URL;

import com.biobam.blast2go.api.datatype.B2GObject;
import com.biobam.blast2go.api.datatype.B2GObjectCreator;
import com.biobam.blast2go.api.datatype.B2GObjectInfo;
import com.biobam.blast2go.api.datatype.B2GObjectValue;
import com.biobam.blast2go.api.utils.FileUtils;

public class PsortbObjectCreator implements B2GObjectCreator {

	@Override
	public B2GObject create(final B2GObjectInfo objectInfo) {
		return new PsortbObject(objectInfo);
	}

	@Override
	public Class<? extends B2GObjectValue> getValueClass() {
		return PsortbObjectValue.class;
	}

	@Override
	public Class<? extends B2GObject> getObjectClass() {
		return PsortbObject.class;
	}

	@Override
	public URL getIcon() {
		return FileUtils.getResource("/res/psortb.png", getClass());
	}

}

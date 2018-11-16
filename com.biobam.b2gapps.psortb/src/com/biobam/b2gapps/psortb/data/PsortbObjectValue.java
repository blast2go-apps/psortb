package com.biobam.b2gapps.psortb.data;

import java.util.Collection;

import com.biobam.blast2go.api.datatype.B2GObject;
import com.biobam.blast2go.api.datatype.B2GObjectValue;

public class PsortbObjectValue extends B2GObjectValue {

	/**
	 * Generated id
	 */
	private static final long serialVersionUID = -1407257839893019088L;

	public final static String ISTORE_KEY = "psort_istore_key";
	Collection<String> sequenceOrder;

	public PsortbObjectValue(final B2GObject bbObject) {
		super(bbObject);
	}

}

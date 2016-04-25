package com.biobam.b2gapps.psortb.data;

import java.io.Serializable;

public class PsortbEntry implements Serializable {

	/**
	 * Generated id.
	 */
	private static final long serialVersionUID = -5626636772000853552L;

	private final String sequenceName;
	private final String location;
	private final double score;

	private PsortbEntry(final String sequenceName, final String location, final double score) {
		if (sequenceName == null) {
			throw new NullPointerException("Sequence name can not be null");
		}
		if (location == null) {
			throw new NullPointerException("Location can not be null");
		}
		this.sequenceName = sequenceName;
		this.location = location;
		this.score = score;
	}

	public static PsortbEntry create(final String sequenceName, final String location, final double score) {
		return new PsortbEntry(sequenceName, location, score);
	}

	public static PsortbEntry createEmpty(final String sequenceName) {
		return new PsortbEntry(sequenceName, "-", 0d);
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public String getLocation() {
		return location;
	}

	public double getScore() {
		return score;
	}
}

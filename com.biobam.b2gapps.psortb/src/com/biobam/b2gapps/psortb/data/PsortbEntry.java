package com.biobam.b2gapps.psortb.data;

import java.io.Serializable;

public class PsortbEntry implements Serializable {

	/**
	 * Generated id.
	 */
	private static final long serialVersionUID = -5626636772000853552L;

	private final String sequenceName;
	private String finalLocalization;
	private String secondaryLocation;
	private double finalScore;
	private double cytoplasmicScore;
	private double cytoplasmicMembraneScore;
	private double cellwallScore;
	private double extracellularScore;
	public double periplasmicScore;
	public double outerMembraneScore;

	private PsortbEntry(final String sequenceName) {
		if (sequenceName == null) {
			throw new NullPointerException("Sequence name can not be null");
		}
		this.sequenceName = sequenceName;
		//		if (location == null) {
		//			throw new NullPointerException("Location can not be null");
		//		}
		//		this.sequenceName = sequenceName;
		//		this.location = location;
		//		this.score = score;
	}

	public static class Builder {
		private PsortbEntry entry;

		public Builder(final String sequenceName) {
			entry = new PsortbEntry(sequenceName);
		}

		public Builder setFinalLocalization(String finalLocalization) {
			entry.finalLocalization = finalLocalization;
			return this;
		}

		public Builder setFinalScore(double finalScore) {
			entry.finalScore = finalScore;
			return this;
		}

		public Builder setSecondaryLocalization(String secondaryLocalization) {
			entry.secondaryLocation = secondaryLocalization;
			return this;
		}

		public Builder setCytoplasmicScore(double cytoplasmicScore) {
			entry.cytoplasmicScore = cytoplasmicScore;
			return this;
		}

		public Builder setCytoplasmicMembraneScore(double cytoplasmicMembraneScore) {
			entry.cytoplasmicMembraneScore = cytoplasmicMembraneScore;
			return this;
		}

		public Builder setCellwallScore(double cellwallScore) {
			entry.cellwallScore = cellwallScore;
			return this;
		}

		public Builder setExtracellularScore(double extracellularScore) {
			entry.extracellularScore = extracellularScore;
			return this;
		}

		public PsortbEntry build() {
			return entry;
		}

		public void setPeriplasmicScore(double periplasmicScore) {
			entry.periplasmicScore = periplasmicScore;
		}

		public void setOuterMembraneScore(double outerMembraneScore) {
			entry.outerMembraneScore = outerMembraneScore;
		}

	}

	public static PsortbEntry.Builder builder(final String sequenceName) {
		//		return new PsortbEntry(sequenceName, location, score);
		return new Builder(sequenceName);
	}

	public static PsortbEntry createEmpty(final String sequenceName) {
		//		return new PsortbEntry(sequenceName, "-", 0d);
		return new Builder(sequenceName).build();
	}

	public PsortbEntry cloneEntryWithNewName(String newName) {
		Builder eb = builder(newName);
		eb.setCellwallScore(cellwallScore);
		eb.setCytoplasmicMembraneScore(cytoplasmicMembraneScore);
		eb.setCytoplasmicScore(cytoplasmicScore);
		eb.setExtracellularScore(extracellularScore);
		eb.setFinalLocalization(finalLocalization);
		eb.setFinalScore(finalScore);
		eb.setSecondaryLocalization(secondaryLocation);
		return eb.build();
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public double getCellwallScore() {
		return cellwallScore;
	}

	public double getCytoplasmicMembraneScore() {
		return cytoplasmicMembraneScore;
	}

	public double getCytoplasmicScore() {
		return cytoplasmicScore;
	}

	public double getExtracellularScore() {
		return extracellularScore;
	}

	public String getFinalLocalization() {
		return finalLocalization;
	}

	public double getFinalScore() {
		return finalScore;
	}

	public String getSecondaryLocation() {
		return secondaryLocation;
	}

	public double getPeriplasmicScore() {
		return periplasmicScore;
	}

	public double getOuterMembraneScore() {
		return outerMembraneScore;
	}

}

package com.biobam.b2gapps.psortb.algo;

enum PSParameters {
	P_ORGANISM("organism"),
	P_GRAM("gram"),
	P_ADVANCED_GRAM("advancedgram"),
	P_CUTOFF("cutoff"),
	P_FILE_NAME("filename");

	private String key;

	private PSParameters(final String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}
}
package com.biobam.b2gapps.psortb.algo;

import com.biobam.blast2go.api.job.parameters.key.additional.ListKeyOption;

public class PsortbParametersHelper {

	/**
	 * organism
	 *
	 */
	public enum ORGANISM implements ListKeyOption {
		BACTERIA("bacteria", "Bacteria"),
		ARCHAEA("archaea", "Archaea");

		private final String id;
		private final String displayName;

		private ORGANISM(final String id, final String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}
	}

	/**
	 * gram
	 */
	public enum GRAM implements ListKeyOption {
		NEGATIVE("negative", "Negative"),
		POSITIVE("positive", "Positive"),
		ADVANCED("advanced", "Advanced");

		private final String id;
		private final String displayName;

		private GRAM(final String id, final String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}
	}

	/**
	 *
	 * advancedgram
	 */
	public enum ADVANCED_GRAM implements ListKeyOption {
		NEGATIVE("negative", "Negative without Outer Membrane"),
		POSITIVE("positive", "Positive with Outer Membrane");

		private final String id;
		private final String displayName;

		private ADVANCED_GRAM(final String id, final String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}
	}

	/**
	 * format
	 *
	 */
	public enum OUTPUT_FORMAT implements ListKeyOption {
		NORMAL("html", "Normal"),
		SHORT_TAB("terse", "Short Format (Tab Delimited)"),
		LONG_TAB("long", "Long Format (Tab Delimited)");

		private final String id;
		private final String displayName;

		private OUTPUT_FORMAT(final String id, final String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}
	}

}

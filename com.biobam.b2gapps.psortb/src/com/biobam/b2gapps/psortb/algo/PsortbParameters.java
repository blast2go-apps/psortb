package com.biobam.b2gapps.psortb.algo;

import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.ADVANCED_GRAM;
import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.GRAM;
import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.ORGANISM;
import com.biobam.blast2go.api.job.parameters.Parameters;
import com.biobam.blast2go.api.job.parameters.key.DoubleKey;
import com.biobam.blast2go.api.job.parameters.key.ListKey;
import com.biobam.blast2go.api.job.parameters.key.NoteKey;
import com.biobam.blast2go.api.job.parameters.key.validator.DoubleValidator;
import com.biobam.blast2go.api.job.parameters.keys.internal.ParameterKey;

public class PsortbParameters extends Parameters {

	public PsortbParameters() {
		add(note);
		add(organism);
		add(gram);
		add(advancedGram);
		add(cutoff);
		//		add(outputFormat);
	}

	public NoteKey note = NoteKey.builder(getBaseName(".note"))
	        .setDescription("PSORTb is the most precise bacterial localization prediction tool available. PSORTb requires that a PROTEIN sequence be submitted.")
	        .build();

	public ListKey<ORGANISM> organism = ListKey.builder(getBaseName(".organism"), ORGANISM.class, ORGANISM.values(), ORGANISM.BACTERIA)
	        .setName("Organism Type")
	        .setDescription("Choose your organism type.")
	        .build();

	public ListKey<GRAM> gram = ListKey.builder(getBaseName(".gram"), GRAM.class, GRAM.values(), GRAM.NEGATIVE)
	        .setName("Gram Stain")
	        .setDescription("PSORTb performs different analyses depending on the class of organism. You are required to choose the appropriate Gram-stain and organism domain (Bacteria or Archaea) for your sequences.")
	        .build();

	public ListKey<ADVANCED_GRAM> advancedGram = ListKey.builder(getBaseName(".advancedGram"), ADVANCED_GRAM.class, ADVANCED_GRAM.values(), ADVANCED_GRAM.NEGATIVE)
	        .setName("Advanced Gram Stain Options")
	        .setDescription("There are some organisms whose Gram stains do not accurately reflect their cellular structure. Two additional analysis options are provided for these organisms by PSORTb:\n - Positive with outer membrane and\n - Negative without outer membrane\n\nUsers can choose to analyze organisms that stain Gram-positive but also have an outer membrane, such as Deinococcus radiodurans, Mycobacterium spp, and Veillonellaceae family of the Firmicutes phylum. The latter option allows users to analyze organisms that stain Gram-negative but have no outer membrane, such as organisms of the Tenericutes phylum, eg. Mycoplasma spp.")
	        .build();

	public DoubleKey cutoff = DoubleKey.builder(getBaseName("cutoff"), 7.5d)
	        .setName("Cutoff")
	        .setDescription("Sets a cutoff value for reported results. Values are between 0 and 10.\nPSORTb v.3.0 returns the five localization sites and the associated probability value for each. The PSORTb 3.0 documentation consider 7.5 to be a good cutoff above which a single localization can be assigned, and our precision and recall values for the program are calculated using this cutoff by default.\nIn certain cases, two localization sites may both exhibit high scores, which may indicate a protein with domains present in neighbouring localization sites. In cases where a localization site has a score between 4.5 (for Gram-negative) and 5.0 (for Gram-positive) and 7.49, the result returned to the user will say \"Unknown - This protein may have multiple localization sites\". In cases like these, we recommend you examine each location's result score to draw your own conclusion.")
	        .setValidator(DoubleValidator.inRange(0, 10))
	        .build();

	@Override
	public boolean isEnabled(ParameterKey<?> parameterKey) {
		if (parameterKey == gram) {
			return organism.getValue() == ORGANISM.BACTERIA;
		}
		if (parameterKey == advancedGram) {
			return organism.getValue() == ORGANISM.BACTERIA && gram.getValue() == GRAM.ADVANCED;
		}
		return true;
	}

}

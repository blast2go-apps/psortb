package com.biobam.b2gapps.psortb.algo;

import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.ADVANCED_GRAM;
import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.GRAM;
import com.biobam.b2gapps.psortb.algo.PsortbParametersHelper.ORGANISM;
import com.biobam.blast2go.api.job.parameters.Parameters;
import com.biobam.blast2go.api.job.parameters.key.ListKey;
import com.biobam.blast2go.api.job.parameters.key.NoteKey;

public class PsortbParameters extends Parameters {

	public PsortbParameters() {
		add(note);
		add(organism);
		add(gram);
		add(advancedGram);
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

	//	public ListKey<OUTPUT_FORMAT> outputFormat = ListKey.builder(getBaseName(".outputFormat"), OUTPUT_FORMAT.class, OUTPUT_FORMAT.values(), OUTPUT_FORMAT.SHORT_TAB)
	//			.setName("Output format ")
	//			.build();

}

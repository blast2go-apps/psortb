package com.biobam.b2gapps.psortb.algo;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.parameters.Parameters;
import com.biobam.blast2go.api.job.parameters.key.FileObjectKey;
import com.biobam.blast2go.api.job.parameters.key.NoteKey;
import com.biobam.blast2go.api.job.parameters.key.additional.FileObjectTypeFilter;
import com.biobam.blast2go.api.job.parameters.key.validator.PathValidator;

public class MergePsortbParameters extends Parameters {

	public MergePsortbParameters() {
		add(note);
		add(file);
	}

	public NoteKey note = NoteKey
			.builder(getBaseName(".note"))
			.setDescription(
					"Merge PSORTb gene ontology terms with project annotations.")
			.build();

	public FileObjectKey file = FileObjectKey
			.builder(getBaseName(".file"))
			.addObjectFilter(
					new FileObjectTypeFilter(PsortbObject.class,
							"PSORTb Results")).setName("PSORTb Results File")
			.setDescription("PSORTb Results File")
			.setValidator(PathValidator.existingFile()).build();

}

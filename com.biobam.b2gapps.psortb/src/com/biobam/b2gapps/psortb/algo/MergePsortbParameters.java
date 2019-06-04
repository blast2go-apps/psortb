package com.biobam.b2gapps.psortb.algo;

import com.biobam.blast2go.api.job.parameters.Parameters;
import com.biobam.blast2go.api.job.parameters.key.FileObjectKey;
import com.biobam.blast2go.api.job.parameters.key.NoteKey;
import com.biobam.blast2go.api.job.parameters.key.additional.FileObjectTypeFilter;
import com.biobam.blast2go.api.job.parameters.key.validator.PathValidator;

import es.blast2go.data.IProject;

public class MergePsortbParameters extends Parameters {

	public MergePsortbParameters() {
		add(note);
		add(file);
	}

	public NoteKey note = NoteKey.builder(getBaseName(".note"))
	        .setDescription("Merge PSORTb gene ontology terms with project annotations.")
	        .build();

	public FileObjectKey file = FileObjectKey.builder(getBaseName(".file"))
	        .addObjectFilter(new FileObjectTypeFilter(IProject.class, "B2G Project"))
	        .setName("Blast2GO Project File")
	        .setDescription("This should be a Blast2GO project while. The GOs from the PSORTb will be merged in the Blast2GO project.")
	        .setValidator(PathValidator.existingFile())
	        .build();

}

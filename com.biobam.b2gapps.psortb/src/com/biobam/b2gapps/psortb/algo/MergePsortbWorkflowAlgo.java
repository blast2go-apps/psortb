package com.biobam.b2gapps.psortb.algo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.B2GModifyJob;
import com.biobam.blast2go.api.job.IB2GProgressMonitor;
import com.biobam.blast2go.api.job.parameters.EmptyParameters;

import es.blast2go.data.IProject;

public class MergePsortbWorkflowAlgo extends B2GModifyJob<EmptyParameters> {
	private static final Logger log = LoggerFactory.getLogger(MergePsortbWorkflowAlgo.class);

	public MergePsortbWorkflowAlgo() {
		super("Merge PSORTb GOs to Annotation", EmptyParameters.INSTANCE);
	}

	@Override
	public void run() throws InterruptedException {
		IProject project = getInput(MergePsortbWorkflowJobMetadata.INPUT_PROJECT);
		PsortbObject psortbObject = getInput(MergePsortbWorkflowJobMetadata.INPUT_PSORTB_RESULT);
		beginTask(getName(), psortbObject.getResults()
		        .size());
		MergePsortbAlgo.merge(project, psortbObject, (IB2GProgressMonitor) getIProgressMonitor());
		addModificationInfo(project);
		postOutput(MergePsortbWorkflowJobMetadata.INPUT_PROJECT, project);
	}
}

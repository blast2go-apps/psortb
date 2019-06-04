package com.biobam.b2gapps.psortb.algo;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.algo.internal.PsortbResultParser;
import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.job.B2GModifyJob;
import com.biobam.blast2go.api.job.parameters.EmptyParameters;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;

import es.blast2go.data.AnnotationResult;
import es.blast2go.data.IProject;

public class MergePsortbWorkflowAlgo extends B2GModifyJob<EmptyParameters> {
	private static final Logger log = LoggerFactory.getLogger(MergePsortbWorkflowAlgo.class);

	public MergePsortbWorkflowAlgo() {
		super("Merge PSORTb GOs to Annotation", EmptyParameters.INSTANCE);
	}

	@Override
	public void run() throws InterruptedException {

		final IProject project = getInput(MergePsortbWorkflowJobMetadata.INPUT_PROJECT);
		final PsortbObject psortbObject = getInput(MergePsortbWorkflowJobMetadata.INPUT_PSORTB_RESULT);

		List<String> idList = psortbObject.getIdList();
		beginTask(getName(), psortbObject.getResults()
		        .size());

		int newAnnots = 0;

		for (String entryId : idList) {
			worked(1);
			final PsortbEntry psortbEntry = psortbObject.getEntry(entryId);
			if (psortbEntry == null) {
				continue;
			}
			final String sequenceName = psortbEntry.getSequenceName();
			if (!project.contains(sequenceName)) {
				log.warn("Project does not contain the sequence {}", sequenceName);
				continue;
			}

			final String location = psortbEntry.getFinalLocalization();
			if (!PsortbResultParser.LOCATION_TO_GOID_MAP.containsKey(location)) {
				log.info("Unknown location: " + location);
				continue;
			}
			final String goID = PsortbResultParser.LOCATION_TO_GOID_MAP.get(location);
			final ILightSequence sequence = project.findSequence(sequenceName);
			if (sequence.hasConditions(SeqCondImpl.COND_HAS_ANNOT_RESULT)) {
				final List<String> currentAnnotation = sequence.getAnnotr()
				        .getGOs();
				if (!currentAnnotation.contains(goID)) {
					currentAnnotation.add(goID);
					postJobMessage(goID + " added to sequence " + sequenceName);
					newAnnots++;
				}
			} else {
				final AnnotationResult annotationResult = new AnnotationResult();
				final Vector<String> gos = new Vector<String>();
				gos.add(goID);
				annotationResult.put("goacc", gos);
				sequence.setAnnotr(annotationResult);
				postJobMessage(goID + " added to sequence " + sequenceName);
				newAnnots++;
			}
			project.updateSequence(sequence);
		}

		addModificationInfo(project);
		postOutputResults(project);
		setFinishMessage(newAnnots + " annotations added to the project.");
	}
}

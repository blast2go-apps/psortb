package com.biobam.b2gapps.psortb.algo;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.algo.internal.PsortbResultParser;
import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.datatype.B2GPersistenceManager;
import com.biobam.blast2go.api.job.B2GJob;
import com.biobam.blast2go.api.job.IB2GProgressMonitor;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import es.blast2go.data.AnnotationResult;
import es.blast2go.data.IB2GObjectProject;
import es.blast2go.data.IProject;

public class MergePsortbAlgo extends B2GJob<MergePsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(MergePsortbAlgo.class);

	public MergePsortbAlgo() {
		super("Merge PSORTb GOs to Annotation", new MergePsortbParameters());
	}

	@Override
	public void run() throws InterruptedException {
		try {
			IB2GObjectProject project = (IB2GObjectProject) B2GPersistenceManager.loadAndFetchWithReference(getParameters().file.getValue());
			postOutput(MergePsortbJobMetadata.PROJECT, project);
			// on Linux the dirty listener is not attached fast enough and therefore the project can appear clean although it has been altered. 
			Thread.sleep(2000);
			PsortbObject psortbObject = getInput(MergePsortbJobMetadata.INPUT_PSORTB_RESULT);
			beginTask(getName(), psortbObject.getResults()
			        .size());
			merge(project, psortbObject, (IB2GProgressMonitor) getIProgressMonitor());
			addModificationInfo(project);
		} catch (IOException e) {
			log.warn("", e);
			terminateWithError("Problem while loading project.", e);
		}
	}

	static public void merge(IProject project, PsortbObject psortbObject, IB2GProgressMonitor monitor) {
		List<String> idList = psortbObject.getIdList();
		Multiset<String> locations = HashMultiset.create();
		int newAnnots = 0;
		for (String entryId : idList) {
			monitor.worked(1);
			PsortbEntry psortbEntry = psortbObject.getEntry(entryId);
			if (psortbEntry == null) {
				continue;
			}
			String sequenceName = psortbEntry.getSequenceName();
			if (!project.contains(sequenceName)) {
				log.warn("Project does not contain the sequence {}", sequenceName);
				continue;
			}
			String location = psortbEntry.getFinalLocalization();
			locations.add(location);
			if (!PsortbResultParser.containsLocationName(location)) {
				continue;
			}
			String goID = PsortbResultParser.getGoId(location);
			ILightSequence sequence = project.findSequence(sequenceName);
			if (sequence.hasConditions(SeqCondImpl.COND_HAS_ANNOT_RESULT)) {
				List<String> currentAnnotation = sequence.getAnnotr()
				        .getGOs();
				if (!currentAnnotation.contains(goID)) {
					currentAnnotation.add(goID);
					newAnnots++;
				}
			} else {
				AnnotationResult annotationResult = new AnnotationResult();
				Vector<String> gos = new Vector<String>();
				gos.add(goID);
				annotationResult.put("goacc", gos);
				sequence.setAnnotr(annotationResult);
				newAnnots++;
			}
			project.updateSequence(sequence);
		}
		monitor.postJobMessage(String.format("Unknown Locations: %d", locations.count("Unknown")));
		monitor.setFinishMessage(newAnnots + " GO Annotations added to the project.");
	}
}

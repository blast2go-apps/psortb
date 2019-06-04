package com.biobam.b2gapps.psortb.algo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biobam.b2gapps.psortb.algo.internal.PsortbResultParser;
import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.datatype.B2GPersistenceManager;
import com.biobam.blast2go.api.job.B2GJob;
import com.biobam.blast2go.project.model.interfaces.ILightSequence;
import com.biobam.blast2go.project.model.interfaces.SeqCondImpl;

import es.blast2go.data.AnnotationResult;
import es.blast2go.data.IProject;

public class MergePsortbAlgo extends B2GJob<MergePsortbParameters> {
	private static final Logger log = LoggerFactory.getLogger(MergePsortbAlgo.class);
	private PsortbObject psortbObject;

	public MergePsortbAlgo(PsortbObject psortbObject) {
		this();
		this.psortbObject = psortbObject;
	}

	public MergePsortbAlgo() {
		super("Merge PSORTb GOs to Annotation", new MergePsortbParameters());
	}

	@Override
	public void run() throws InterruptedException {
		boolean initializedElsewhere = true;
		try {
			if (psortbObject == null) {
				// not initialized by constructor
				initializedElsewhere = false;
				psortbObject = (PsortbObject) B2GPersistenceManager.loadAndFetchWithReference(getParameters().file.getValue());
			}

			final IProject project = getInput(MergePsortbJobMetadata.INPUT_PROJECT);

			if (project.getSelectedSequencesCount() == 0) {
				setFinishMessage("No sequences selected.");
				return;
			}

			beginTask(getName(), psortbObject.getResults()
			        .size());

			int newAnnots = 0;
			for (final Iterator<Entry<String, PsortbEntry>> iterator = psortbObject.getResults()
			        .entryIterator(); iterator.hasNext();) {
				worked(1);
				final Entry<String, PsortbEntry> entry = iterator.next();
				final PsortbEntry psortbEntry = entry.getValue();
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
//<<<<<<< HEAD
			setFinishMessage(newAnnots + " annotations added to the project.");
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (!initializedElsewhere) {
				try {
					psortbObject.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
//=======
//			postOutputResults(project);
//			setFinishMessage(newAnnots + " annotations added to the project.");
//		} catch (IOException e) {
//			log.warn("", e);
//			terminateWithError("Problem while loading project.", e);
//>>>>>>> origin/master
		}
	}
}

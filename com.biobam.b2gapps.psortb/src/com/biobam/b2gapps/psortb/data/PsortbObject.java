package com.biobam.b2gapps.psortb.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.biobam.blast2go.api.datatype.AbstractB2GObject;
import com.biobam.blast2go.api.datatype.B2GMergeable;
import com.biobam.blast2go.api.datatype.B2GObjectInfo;
import com.biobam.blast2go.api.datatype.B2GObjectValue;
import com.biobam.blast2go.api.datatype.CollectionsService;
import com.biobam.blast2go.api.store.IStore;

public class PsortbObject extends AbstractB2GObject implements B2GMergeable {

	private IStore<String, PsortbEntry> results;
	/**
	 * Store sequences order for restoring initial order when the IStore is modified.
	 */
	private Collection<String> sequenceOrder;
	/**
	 * Listener for updating generic table.
	 */
	private transient List<PropertyChangeListener> genericTableListeners = new ArrayList<PropertyChangeListener>();

	/**
	 * Property name.
	 */
	public static final String INSERT_ENTRY = "insertEntry";

	PsortbObject(final B2GObjectInfo objectInfo) {
		super(objectInfo);
	}

	private PsortbObject(final String name, final Map<String, PsortbEntry> results) {
		super(name);
		if (results == null) {
			throw new NullPointerException("Results can not be null");
		}
		this.results = CollectionsService.getInstance()
		        .newStore(getId(), PsortbObjectValue.ISTORE_KEY);
		sequenceOrder = new LinkedHashSet<String>(results.size());
		for (final Entry<String, PsortbEntry> entry : results.entrySet()) {
			this.results.insert(entry.getKey(), entry.getValue());
			sequenceOrder.add(entry.getKey());
		}
	}

	public static PsortbObject newInstance(final String name, final Map<String, PsortbEntry> resultsMap) {
		return new PsortbObject(name, resultsMap);
	}

	@Override
	public String getType() {
		return "PSORTb result";
	}

	@Override
	public void loadValue(final B2GObjectValue value) {
		if (!(value instanceof PsortbObjectValue)) {
			throw new IllegalArgumentException("B2GObjectValue is not a PsortObjectValue");
		}
		final PsortbObjectValue rfamValue = (PsortbObjectValue) value;
		results = CollectionsService.getInstance()
		        .loadStore(getId(), PsortbObjectValue.ISTORE_KEY);
		sequenceOrder = rfamValue.sequenceOrder;
	}

	@Override
	public B2GObjectValue saveValue() {
		final PsortbObjectValue value = new PsortbObjectValue(this);
		value.add(PsortbObjectValue.ISTORE_KEY);
		value.sequenceOrder = sequenceOrder;
		return value;
	}

	public IStore<String, PsortbEntry> getResults() {
		return results;
	}

	public List<String> getIdList() {
		return new ArrayList<>(sequenceOrder);
	}

	public PsortbEntry getEntry(final String entryId) {
		if (entryId == null) {
			throw new NullPointerException("Id can not be null");
		}
		return results.get(entryId);
	}

	public boolean containsEntry(final String seqName) {
		if (seqName == null) {
			throw new NullPointerException("Id can not be null");
		}
		return results.containsKey(seqName);
	}

	public void add(final PsortbEntry entry) {
		if (entry == null) {
			throw new NullPointerException("Results can not be null");
		}
		startModification();
		try {
			final String sequenceID = entry.getSequenceName();
			sequenceOrder.add(sequenceID);
			results.insert(sequenceID, entry);
			notifyGenericListeners(INSERT_ENTRY, sequenceID, sequenceID);
			setDirty(true);
		} finally {
			endModification();
		}
	}

	public void addChangeListener(final PropertyChangeListener newListener) {
		genericTableListeners.add(newListener);
	}

	public void removeChangeListener(final PropertyChangeListener listenerToRemove) {
		genericTableListeners.remove(listenerToRemove);
	}

	private void notifyGenericListeners(final String property, final String oldValue, final String newValue) {
		for (final PropertyChangeListener changeListener : genericTableListeners) {
			changeListener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}

	@Override
	public B2GMergeable mergeWith(final List<B2GMergeable> otherObjects, final HANDLE_DOUBLES handleDoubles) {
		final Map<String, PsortbEntry> mergedData = new LinkedHashMap<String, PsortbEntry>();
		for (final Iterator<Entry<String, PsortbEntry>> entryIterator = results.entryIterator(); entryIterator.hasNext();) {
			final Entry<String, PsortbEntry> entry = entryIterator.next();
			mergedData.put(entry.getKey(), entry.getValue());
		}
		for (final B2GMergeable object : otherObjects) {
			final PsortbObject psortObject = (PsortbObject) object;
			for (final Iterator<Entry<String, PsortbEntry>> entryIterator = psortObject.getResults()
			        .entryIterator(); entryIterator.hasNext();) {
				final Entry<String, PsortbEntry> entry = entryIterator.next();
				final String key = entry.getKey();
				switch (handleDoubles) {
				case SKIP:
					if (!mergedData.containsKey(key)) {
						mergedData.put(key, entry.getValue());
					}
					break;
				case ADD:
					String sequenceID = key;
					PsortbEntry value = entry.getValue();
					if (mergedData.containsKey(key)) {
						sequenceID = key + "_from_" + psortObject.getName();
						value = value.cloneEntryWithNewName(sequenceID);
						//						value = PsortbEntry.builder(sequenceID).
						//						value = PsortbEntry.create(sequenceID, value.getLocation(), value.getScore());
					}
					mergedData.put(sequenceID, value);
					break;
				default:
					break;
				}
			}
		}
		return PsortbObject.newInstance(getName() + " merged", mergedData);
	}
}

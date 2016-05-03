package com.biobam.b2gapps.psortb.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.biobam.b2gapps.psortb.algo.internal.PsortbResultParser;
import com.biobam.b2gapps.psortb.data.PsortbEntry;
import com.biobam.b2gapps.psortb.data.PsortbObject;
import com.biobam.blast2go.api.core.utils.callback.CallBack;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.ColumnDataCreator;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.IB2GTableFormat;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.IColumnData;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.ITableModelChangedCallback;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.ITableTag;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.TableTag;
import com.biobam.blast2go.api.dataviewer.interfaces.tableformat.TagColor;

public class PsortbGenericTableController implements IB2GTableFormat<PsortbObject, PsortbEntry>, PropertyChangeListener {

	private PsortbObject object;
	private ITableModelChangedCallback changeCallback;

	public PsortbGenericTableController(final PsortbObject object) {
		this.object = object;
		this.object.addChangeListener(this);
	}

	@Override
	public PsortbObject getObject() {
		return object;
	}

	@Override
	public void updateObject(final PsortbObject newObject) {
		object = newObject;
	}

	private static final ITableTag RESULT_TAG = TableTag.create("PSORTb", TagColor.BLUE);
	private static final ITableTag NO_RESULT_TAG = TableTag.create("UNKNOWN", TagColor.RED);

	@Override
	public List<IColumnData<?>> getColumnList() {
		final List<IColumnData<?>> columns = new ArrayList<IColumnData<?>>();
		columns.add(ColumnDataCreator.stringColumnBuilder("Sequence Name")
		        .setContentCallback(new CallBack<String, String>() {

			        @Override
			        public String call(final String seqName) {
				        if (object.containsEntry(seqName)) {
					        return seqName;
				        }
				        return null;
			        }
		        })
		        .setMinWidth(70)
		        .setPrefferedWidth(150)
		        .setMaxWidth(300)
		        .build());

		columns.add(ColumnDataCreator.stringColumnBuilder("Final Localization")
		        .setContentCallback(new CallBack<String, String>() {

			        @Override
			        public String call(final String id) {
				        return getEntry(id).getFinalLocalization();
			        }
		        })
		        .setMinWidth(120)
		        .setPrefferedWidth(300)
		        .setMaxWidth(400)
		        .build());
		
		columns.add(ColumnDataCreator.doubleColumnBuilder("Final Score")
		        .setContentCallback(new CallBack<String, Double>() {

			        @Override
			        public Double call(final String id) {
				        return getEntry(id).getFinalScore();
			        }
		        })
		        .setMinWidth(70)
		        .setPrefferedWidth(70)
//		        .setMaxWidth(70)
		        .build());
		
		columns.add(ColumnDataCreator.stringColumnBuilder("GO ID")
		        .setContentCallback(new CallBack<String, String>() {

			        @Override
			        public String call(final String id) {
				        final String location = getEntry(id).getFinalLocalization();
				        if (!PsortbResultParser.LOCATION_TO_GOID_MAP.containsKey(location)) {
					        return "-";
				        }
				        return PsortbResultParser.LOCATION_TO_GOID_MAP.get(location);
			        }
		        })
		        .setMinWidth(80)
		        .setPrefferedWidth(80)
		        .setMaxWidth(120)
		        .build());
		
		
		columns.add(ColumnDataCreator.stringColumnBuilder("Secondary Localization")
				.setContentCallback(new CallBack<String, String>() {
					
					@Override
					public String call(final String id) {
						return getEntry(id).getSecondaryLocation();
					}
				})
				.setMinWidth(120)
				.setPrefferedWidth(300)
				.setMaxWidth(400)
				.setDefaultInvisible()
				.build());
		

		columns.add(ColumnDataCreator.doubleColumnBuilder("Cytoplasmic Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getCytoplasmicScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());
		
		columns.add(ColumnDataCreator.doubleColumnBuilder("CytoplasmicMembrane Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getCytoplasmicMembraneScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());
		
		columns.add(ColumnDataCreator.doubleColumnBuilder("Cellwall Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getCellwallScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());
		columns.add(ColumnDataCreator.doubleColumnBuilder("Extracellular Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getExtracellularScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());
		
		columns.add(ColumnDataCreator.doubleColumnBuilder("Periplasmic Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getPeriplasmicScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());
		
		columns.add(ColumnDataCreator.doubleColumnBuilder("OuterMembrane Score")
				.setContentCallback(new CallBack<String, Double>() {
					
					@Override
					public Double call(final String id) {
						return getEntry(id).getOuterMembraneScore();
					}
				})
				.setMinWidth(70)
				.setPrefferedWidth(70)
//				.setMaxWidth(70)
				.setDefaultInvisible()
				.build());

		columns.add(ColumnDataCreator.tagColumnBuilder()
		        .setContentCallback(new CallBack<String, List<ITableTag>>() {
			        @Override
			        public List<ITableTag> call(final String seqId) {
				        //				        final PsortbEntry entry = object.getEntry(seqId);
				        //				        final String location = entry.getLocation();
				        //				        if (!PsortbShortResultParser.LOCATION_TO_TAG_MAP.containsKey(location)) {
				        //					        return Collections.emptyList();
				        //				        }
				        //				        return Arrays.asList(PsortbShortResultParser.LOCATION_TO_TAG_MAP.get(location));
				        final PsortbEntry entry = object.getEntry(seqId);
				        final String location = entry.getFinalLocalization();
				        if (location.isEmpty() || location.equals("-") || location.equals("Unknown")) {
					        return Collections.emptyList();
					        //					        return Arrays.asList(NO_RESULT_TAG);
				        } else {
					        return Arrays.asList(RESULT_TAG);
				        }
			        }
		        })
		        .build());
		return columns;
	}

	@Override
	public List<String> getIdList() {
		return object.getIdList();
	}

	@Override
	public void setList(final List<String> ids) {}

	@Override
	public PsortbEntry getEntry(final String entryId) {
		return object.getEntry(entryId);
	}

	@Override
	public void setChangeCallback(final ITableModelChangedCallback changeCallback) {
		if (changeCallback == null) {
			throw new NullPointerException("ITableModelChangedCallback can not be null");
		}
		this.changeCallback = changeCallback;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (changeCallback == null) {
			return;
		}
		final String eventName = event.getPropertyName();
		if (eventName.equals(PsortbObject.UPDATE_ENTRY)) {
			changeCallback.changedEntry((String) event.getOldValue());
		}
	}

	@Override
	public void viewerClosing() {}

	@Override
	public PsortbObject extractFrom(final List<String> ids) {
		final Map<String, PsortbEntry> newMap = new LinkedHashMap<>();
		for (final String string : ids) {
			newMap.put(string, object.getEntry(string));
		}
		return PsortbObject.newInstance(object.getName() + " extraction", newMap);
	}

}

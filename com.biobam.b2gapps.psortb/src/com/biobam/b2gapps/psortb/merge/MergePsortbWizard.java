package com.biobam.b2gapps.psortb.merge;

import org.eclipse.swt.widgets.Composite;

import com.biobam.blast2go.api.wizard.B2GWizard;
import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.FileWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.SpaceWidget;

public class MergePsortbWizard extends B2GWizard<MergePsortbParameters> {

	public MergePsortbWizard() {
		setWindowTitle("Merge PSORTb GOs to Annotation");
	}

	@Override
	protected void definePages() {
		addPage(new B2GWizardPage("Merge PSORTb GOs to Annotation") {

			@Override
			public void definePage(final Composite parent) {
				final MergePsortbParameters parameters = getParameters();

				addWidget(NoteWidget.create(parent, parameters.note));
				addWidget(SpaceWidget.create(parent));
				addWidget(FileWidget.createObjectOpen(parent, parameters.file));
			}
		});
	}
}

package com.biobam.b2gapps.psortb.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;

import com.biobam.b2gapps.psortb.algo.PsortbParameters;
import com.biobam.blast2go.api.wizard.B2GWizard;
import com.biobam.blast2go.api.wizard.page.B2GWizardPage;
import com.biobam.blast2go.api.wizard.page.widget.implementations.ComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.DoubleWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.IComboWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.NoteWidget;
import com.biobam.blast2go.api.wizard.page.widget.implementations.SpaceWidget;

public class PsortbWizard extends B2GWizard<PsortbParameters> {

	public PsortbWizard() {
		setWindowTitle("Run PSORTb");
	}

	@Override
	protected void definePages() {
		addPage(new B2GWizardPage("PSORTb Configuration") {

			@Override
			public void definePage(final Composite parent) {
				final PsortbParameters parameters = getParameters();

				addWidget(NoteWidget.create(parent, parameters.note));
				addWidget(SpaceWidget.create(parent));
				addWidget(ComboWidget.createSimple(parent, parameters.organism));
				final IComboWidget gramWidget = addWidget(ComboWidget.createSimple(parent, parameters.gram));
				final IComboWidget advancedGramWidget = addWidget(ComboWidget.createSimple(parent, parameters.advancedGram));

				parameters.organism.addPropertyChangeListener("value", new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						//						final ORGANISM newValue = (ORGANISM) evt.getNewValue();
						configureGramWidget(gramWidget, parameters);
						configureAdvancedGramWidget(advancedGramWidget, parameters);
					}

				});

				parameters.gram.addPropertyChangeListener("value", new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						//						final GRAM newValue = (GRAM) evt.getNewValue();
						configureAdvancedGramWidget(advancedGramWidget, parameters);
					}

				});

				configureGramWidget(gramWidget, parameters);
				configureAdvancedGramWidget(advancedGramWidget, parameters);

				addWidget(DoubleWidget.create(parent, parameters.cutoff));

			}

			private void configureGramWidget(final IComboWidget gramWidget, final PsortbParameters parameters) {
				gramWidget.setEnabled(parameters.isEnabled(parameters.gram));
			}

			private void configureAdvancedGramWidget(IComboWidget advancedGramWidget, PsortbParameters parameters) {
				advancedGramWidget.setEnabled(parameters.isEnabled(parameters.advancedGram));
			}
		});

	}

}

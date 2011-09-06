/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag;

import static com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag.Messages.*;
import static java.util.Arrays.asList;
import static org.eclipse.xtext.util.Strings.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.binding.*;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;

/**
 * Preference page where users can specify the patterns to use to match comments where "the next id" is being tracked.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NumericTagPreferencePage extends PreferenceAndPropertyPage {

  private static final String PATTERN_DELIMITER = "//t"; //$NON-NLS-1$
  private static final String PREFERENCE_PAGE_ID = NumericTagPreferencePage.class.getName();

  private List lstPaths;
  private Button btnAdd;
  private Button btnEdit;
  private Button btnRemove;

  @Override protected void doCreateContents(Composite parent) {
    Label lblDescription = new Label(parent, SWT.WRAP);
    GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
    gridData.widthHint = 150; // only expand further if anyone else requires it
    lblDescription.setLayoutData(gridData);
    lblDescription.setText(pageDescription);
    new Label(parent, SWT.NONE);

    ListViewer lstVwrPaths = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
    lstPaths = lstVwrPaths.getList();
    gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gridData.heightHint = 127;
    lstPaths.setLayoutData(gridData);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
    composite.setLayout(new GridLayout(1, false));

    btnAdd = new Button(composite, SWT.NONE);
    btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnAdd.setText(add);

    btnEdit = new Button(composite, SWT.NONE);
    btnEdit.setEnabled(false);
    btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnEdit.setText(edit);

    btnRemove = new Button(composite, SWT.NONE);
    btnRemove.setEnabled(false);
    btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnRemove.setText(remove);

    addEventListeners();
  }

  private void addEventListeners() {
    lstPaths.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableButtonsDependingOnListSelection();
      }
    });
    btnRemove.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        int index = lstPaths.getSelectionIndex();
        if (index < 0) return;
        lstPaths.remove(index);
        enableButtonsDependingOnListSelection();
      }
    });
  }

  private void enableButtonsDependingOnListSelection() {
    int selectionIndex = lstPaths.getSelectionIndex();
    boolean hasSelection = selectionIndex >= 0;
    btnEdit.setEnabled(hasSelection);
    btnRemove.setEnabled(hasSelection);
  }

  @Override protected BooleanPreference enableProjectSettingsPreference(IPreferenceStore store) {
    return null;
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    RawPreferences preferences = new RawPreferences(getPreferenceStore());
    final StringPreference patterns = preferences.patterns();
    preferenceBinder.add(new Binding() {
      public void applyPreferenceValueToTarget() {
        setPatterns(patterns.value());
      }

      public void applyDefaultPreferenceValueToTarget() {
        setPatterns(patterns.defaultValue());
      }

      public void savePreferenceValue() {
        patterns.value(patterns());
      }
    });
  }

  private void setPatterns(String value) {
    lstPaths.removeAll();
    for (String pattern : split(value, PATTERN_DELIMITER))
      lstPaths.add(pattern);
  }

  private String patterns() {
    return concat(PATTERN_DELIMITER, asList(lstPaths.getItems()));
  }

  @Override protected void onProjectSettingsActivation(boolean projectSettingsActive) {}

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}

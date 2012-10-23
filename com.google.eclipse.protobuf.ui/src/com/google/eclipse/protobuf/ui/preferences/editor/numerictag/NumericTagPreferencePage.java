/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.numerictag;

import static org.eclipse.jface.window.Window.OK;

import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.AddOrEditPatternDialog.addPattern;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.AddOrEditPatternDialog.editPattern;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.add;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.edit;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.pageDescription;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.remove;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.PreferenceNames.NUMERIC_TAG_PATTERNS;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToListItems.bindItemsOf;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.google.eclipse.protobuf.ui.preferences.StringSplitter;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceFactory;

/**
 * Preference page where users can specify the patterns to use to match comments where "the next id" is being tracked.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NumericTagPreferencePage extends PreferenceAndPropertyPage {
  private static final String PREFERENCE_PAGE_ID = NumericTagPreferencePage.class.getName();

  private List lstPatterns;
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

    ListViewer lstVwrPatterns = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
    lstPatterns = lstVwrPatterns.getList();
    gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gridData.heightHint = 127;
    lstPatterns.setLayoutData(gridData);

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
    lstPatterns.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableButtonsDependingOnListSelection();
      }
    });
    btnAdd.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        AddOrEditPatternDialog dialog = addPattern(getShell());
        if (dialog.open() == OK) {
          lstPatterns.add(dialog.pattern());
        }
      }
    });
    btnEdit.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        int selectionIndex = lstPatterns.getSelectionIndex();
        AddOrEditPatternDialog dialog = editPattern(lstPatterns.getItem(selectionIndex), getShell());
        if (dialog.open() == OK) {
          lstPatterns.setItem(selectionIndex, dialog.pattern());
        }
      }
    });
    btnRemove.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        int index = lstPatterns.getSelectionIndex();
        if (index < 0) {
          return;
        }
        lstPatterns.remove(index);
        enableButtonsDependingOnListSelection();
      }
    });
  }

  private void enableButtonsDependingOnListSelection() {
    int selectionIndex = lstPatterns.getSelectionIndex();
    boolean hasSelection = selectionIndex >= 0;
    btnEdit.setEnabled(hasSelection);
    btnRemove.setEnabled(hasSelection);
  }

  @Override protected String enableProjectSettingsPreferenceName() {
    return null;
  }

  @Override protected void setupBinding(PreferenceBinder binder, PreferenceFactory factory) {
    StringSplitter splitter = NumericTagPatternSplitter.instance();
    binder.add(bindItemsOf(lstPatterns).to(factory.newStringListPreference(NUMERIC_TAG_PATTERNS, splitter)));
  }

  @Override protected void onProjectSettingsActivation(boolean projectSettingsActive) {}

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}

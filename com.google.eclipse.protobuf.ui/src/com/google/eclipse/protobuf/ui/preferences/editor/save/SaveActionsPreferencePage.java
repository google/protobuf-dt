/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save;

import static com.google.eclipse.protobuf.ui.preferences.editor.save.Messages.inAllLines;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.Messages.inEditedLines;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.Messages.removeTrailingWhitespace;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.IN_ALL_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.IN_EDITED_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.REMOVE_TRAILING_WHITESPACE;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceFactory;
import com.google.inject.Inject;

/**
 * "Save Actions" preference page.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  @Inject private IPreferenceStoreAccess preferenceStoreAccess;

  private final PreferenceBinder preferenceBinder = new PreferenceBinder();

  private Button btnRemoveTrailingWhitespace;
  private Button btnInEditedLines;
  private Button btnInAllLines;

  @Override public void init(IWorkbench workbench) {}

  @Override protected Control createContents(Composite parent) {
    Composite contents = new Composite(parent, NONE);
    contents.setLayout(new GridLayout(1, false));
    btnRemoveTrailingWhitespace = new Button(contents, SWT.CHECK);
    btnRemoveTrailingWhitespace.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
    btnRemoveTrailingWhitespace.setText(removeTrailingWhitespace);

    Composite composite = new Composite(contents, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    composite.setLayout(new GridLayout(1, false));

    btnInEditedLines = new Button(composite, SWT.RADIO);
    btnInEditedLines.setText(inEditedLines);

    btnInAllLines = new Button(composite, SWT.RADIO);
    btnInAllLines.setText(inAllLines);

    setUpBinding();
    preferenceBinder.applyValues();
    updateContents();
    addEventListeners();
    return contents;
  }

  private void setUpBinding() {
    PreferenceFactory factory = new PreferenceFactory(getPreferenceStore());
    preferenceBinder.addAll(
        bindSelectionOf(btnRemoveTrailingWhitespace).to(factory.newBooleanPreference(REMOVE_TRAILING_WHITESPACE)),
        bindSelectionOf(btnInAllLines).to(factory.newBooleanPreference(IN_ALL_LINES)),
        bindSelectionOf(btnInEditedLines).to(factory.newBooleanPreference(IN_EDITED_LINES))
    );
  }

  private void addEventListeners() {
    btnRemoveTrailingWhitespace.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        updateContents();
      }
    });
  }

  private void updateContents() {
    boolean enabled = btnRemoveTrailingWhitespace.getSelection();
    btnInAllLines.setEnabled(enabled);
    btnInEditedLines.setEnabled(enabled);
  }

  @Override protected IPreferenceStore doGetPreferenceStore() {
    return preferenceStoreAccess.getWritablePreferenceStore();
  }

  @Override public boolean performOk() {
    preferenceBinder.saveValues();
    return true;
  }

  @Override protected void performDefaults() {
    preferenceBinder.applyDefaults();
    super.performDefaults();
    updateContents();
  }
}

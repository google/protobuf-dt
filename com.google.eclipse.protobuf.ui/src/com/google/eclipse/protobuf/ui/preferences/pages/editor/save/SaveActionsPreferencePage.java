/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.save;

import static com.google.eclipse.protobuf.ui.preferences.binding.BindingToButtonSelection.bindSelectionOf;

import com.google.eclipse.protobuf.ui.preferences.binding.*;
import com.google.inject.Inject;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

/**
 * "Save Actions" preference page.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  @Inject private IPreferenceStoreAccess preferenceStoreAccess;

  private final PreferenceBinder preferenceBinder = new PreferenceBinder();

  private Button btnRemoveTrailingwhitespace;

  /** {@inheritDoc} */
  public void init(IWorkbench workbench) {}

  @Override protected Control createContents(Composite parent) {
    Composite contents = new Composite(parent, NONE);
    contents.setLayout(new GridLayout(1, false));
    btnRemoveTrailingwhitespace = new Button(contents, SWT.CHECK);
    btnRemoveTrailingwhitespace.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    btnRemoveTrailingwhitespace.setText("Remove trailing &whitespace");
    setUpBinding();
    preferenceBinder.applyValues();
    return contents;
  }

  private void setUpBinding() {
    RawPreferences preferences = new RawPreferences(getPreferenceStore());
    preferenceBinder.addAll(
        bindSelectionOf(btnRemoveTrailingwhitespace).to(preferences.removeTrailingWhitespace())
    );
  }

  /**
   * Returns the preference store of this preference page.
   * @return the preference store.
   */
  @Override protected final IPreferenceStore doGetPreferenceStore() {
    return preferenceStoreAccess.getWritablePreferenceStore();
  }

  @Override public final boolean performOk() {
    preferenceBinder.saveValues();
    return true;
  }

  @Override protected final void performDefaults() {
    preferenceBinder.applyDefaults();
    super.performDefaults();
  }
}

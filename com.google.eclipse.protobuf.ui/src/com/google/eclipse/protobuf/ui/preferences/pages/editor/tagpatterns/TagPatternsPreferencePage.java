/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.tagpatterns;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;
import com.google.eclipse.protobuf.ui.preferences.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ListViewer;

/**
 * Preference page where users can specify the patterns to use to match comments where "the next id" is being tracked.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TagPatternsPreferencePage extends PreferenceAndPropertyPage {
  
  private List lstPaths;
  private Button btnAdd;

  @Override protected void doCreateContents(Composite parent) {
    Label lblDescription = new Label(parent, SWT.NONE);
    GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    // gridData.horizontalSpan = 3;
    gridData.widthHint = 150; // only expand further if anyone else requires it
    lblDescription.setLayoutData(gridData);
    lblDescription.setText("Patterns to match the comments that track the next available tag number in message fields and enum literals.");
    
    ListViewer lstVwrPaths = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
    lstPaths = lstVwrPaths.getList();
    gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
    gridData.heightHint = 121;
    lstPaths.setLayoutData(gridData);
    
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
    composite.setLayout(new GridLayout(1, false));
    
    btnAdd = new Button(composite, SWT.NONE);
    btnAdd.setSize(88, 29);
    btnAdd.setText("&Add");
  }

  @Override protected BooleanPreference enableProjectSettingsPreference(IPreferenceStore store) {
    // TODO(alruiz): Auto-generated method stub
    return null;
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    // TODO(alruiz): Auto-generated method stub

  }

  @Override protected void onProjectSettingsActivation(boolean projectSettingsActive) {
    // TODO(alruiz): Auto-generated method stub

  }

  @Override protected String preferencePageId() {
    // TODO(alruiz): Auto-generated method stub
    return null;
  }
}

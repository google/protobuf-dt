/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.ui.PluginImageHelper;

/**
 * Editor where users can specify which are the target languages for protoc and the location of the output folders for
 * each language.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TargetLanguageOutputDirectoryEditor extends Composite {

  private static final TargetLanguagePreference[] NO_PREFERENCES = new TargetLanguagePreference[0];

  private final Table tblLanguageOutput;
  private final TableViewer tblVwrLanguageOutput;
  private final Button btnEdit;

  private TargetLanguagePreferences preferences;

  public TargetLanguageOutputDirectoryEditor(Composite parent, final PluginImageHelper imageHelper) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(1, false));

    tblVwrLanguageOutput = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);

    tblLanguageOutput = tblVwrLanguageOutput.getTable();
    tblLanguageOutput.setHeaderVisible(true);
    tblLanguageOutput.setLinesVisible(true);
    tblLanguageOutput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    TableViewerColumn tblclmnVwrEnabled = new TableViewerColumn(tblVwrLanguageOutput, SWT.NONE);
    TableColumn tblclmnEnabled = tblclmnVwrEnabled.getColumn();
    tblclmnEnabled.setResizable(false);
    tblclmnEnabled.setWidth(27);
    tblclmnVwrEnabled.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ""; //$NON-NLS-1$
      }

      @Override public Image getImage(Object element) {
        boolean enabled = ((TargetLanguagePreference)element).isEnabled();
        return imageHelper.getImage(enabled ? "checked.gif" : "unchecked.gif"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    });

    TableViewerColumn tblclmnVwrLanguage = new TableViewerColumn(tblVwrLanguageOutput, SWT.NONE);
    TableColumn tblclmnLanguage = tblclmnVwrLanguage.getColumn();
    tblclmnLanguage.setResizable(false);
    tblclmnLanguage.setWidth(100);
    tblclmnLanguage.setText(language);
    tblclmnVwrLanguage.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ((TargetLanguagePreference)element).language().name();
      }
    });

    TableViewerColumn tblclmnVwrOutputDirectory = new TableViewerColumn(tblVwrLanguageOutput, SWT.NONE);
    TableColumn tblclmnOutputDirectory = tblclmnVwrOutputDirectory.getColumn();
    tblclmnOutputDirectory.setResizable(false);
    tblclmnOutputDirectory.setWidth(100);
    tblclmnOutputDirectory.setText(outputDirectory);
    tblclmnVwrOutputDirectory.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ((TargetLanguagePreference)element).outputDirectory();
      }
    });

    btnEdit = new Button(this, SWT.NONE);
    btnEdit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    btnEdit.setText(edit);

    tblVwrLanguageOutput.setContentProvider(ArrayContentProvider.getInstance());
    updateTable();
  }

  public void preferences(TargetLanguagePreferences newPreferences) {
    preferences = newPreferences;
    updateTable();
  }

  private void updateTable() {
    tblVwrLanguageOutput.setInput(preferences());
  }

  private TargetLanguagePreference[] preferences() {
    if (preferences == null) return NO_PREFERENCES;
    TargetLanguagePreference[] languages = new TargetLanguagePreference[3];
    languages[0] = preferences.java();
    languages[1] = preferences.cpp();
    languages[2] = preferences.python();
    return languages;
  }

  /** {@inheritDoc} */
  @Override public void setEnabled(boolean enabled) {
    tblLanguageOutput.setEnabled(enabled);
    btnEdit.setEnabled(enabled);
    super.setEnabled(enabled);
  }
}

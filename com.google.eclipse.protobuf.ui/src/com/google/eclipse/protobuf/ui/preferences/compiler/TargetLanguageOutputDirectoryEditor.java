/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.TargetLanguage.*;

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

  public TargetLanguageOutputDirectoryEditor(Composite parent, final PluginImageHelper imageHelper) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(1, false));

    TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);

    Table table = tableViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    TableViewerColumn tblclmnVwrEnabled = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnEnabled = tblclmnVwrEnabled.getColumn();
    tblclmnEnabled.setResizable(false);
    tblclmnEnabled.setWidth(27);
    tblclmnVwrEnabled.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return "";
      }

      @Override public Image getImage(Object element) {
        boolean enabled = ((TargetLanguagePreference)element).isEnabled();
        return imageHelper.getImage(enabled ? "checked.gif" : "unchecked.gif");
      }
    });
    tblclmnVwrEnabled.setEditingSupport(new TargetLanguageIsEnabledEditor(tableViewer));

    TableViewerColumn tblclmnVwrLanguage = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnLanguage = tblclmnVwrLanguage.getColumn();
    tblclmnLanguage.setResizable(false);
    tblclmnLanguage.setWidth(100);
    tblclmnLanguage.setText("Language");
    tblclmnVwrLanguage.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ((TargetLanguagePreference)element).language().name();
      }
    });

    TableViewerColumn tblclmnVwrOutputDirectory = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnOutputDirectory = tblclmnVwrOutputDirectory.getColumn();
    tblclmnOutputDirectory.setResizable(false);
    tblclmnOutputDirectory.setWidth(100);
    tblclmnOutputDirectory.setText("Output Directory");
    tblclmnVwrOutputDirectory.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ((TargetLanguagePreference)element).outputDirectory();
      }
    });

    tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    tableViewer.setInput(languages());
  }

  private TargetLanguagePreference[] languages() {
    TargetLanguagePreference[] languages = new TargetLanguagePreference[3];
    languages[0] = new TargetLanguagePreference(JAVA, "", true);
    languages[1] = new TargetLanguagePreference(CPP, "", true);
    languages[2] = new TargetLanguagePreference(PYTHON, "", true);
    return languages;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Editor where users can specify which are the target languages for protoc and the location of the output folders for
 * each language.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TargetLanguageOutputDirectoryEditor extends Composite {
  private Table table;

  /**
   * Creates a new </code>{@link TargetLanguageOutputDirectoryEditor}</code>.
   * @param parent a widget which will be the parent of the new instance (cannot be {@code null}.)
   */
  public TargetLanguageOutputDirectoryEditor(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout(1, false));

    TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);

    table = tableViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    TableViewerColumn tblclmnVwrLanguage = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnLanguage = tblclmnVwrLanguage.getColumn();
    tblclmnLanguage.setWidth(100);
    tblclmnLanguage.setText("Language");
    tblclmnVwrLanguage.setLabelProvider(new ColumnLabelProvider() {
      @Override public String getText(Object element) {
        return ((TargetLanguagePreference)element).language();
      }
    });
    tblclmnVwrLanguage.setEditingSupport(new TargetLanguageIsEnabledEditor(tableViewer));

    TableViewerColumn tblclmnVwrOutputDirectory = new TableViewerColumn(tableViewer, SWT.NONE);
    TableColumn tblclmnOutputDirectory = tblclmnVwrOutputDirectory.getColumn();
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
    languages[1] = new TargetLanguagePreference(TargetLanguage.JAVA, "", true);
    languages[0] = new TargetLanguagePreference(TargetLanguage.CPP, "", true);
    languages[2] = new TargetLanguagePreference(TargetLanguage.PYTHON, "", true);
    return languages;
  }
}

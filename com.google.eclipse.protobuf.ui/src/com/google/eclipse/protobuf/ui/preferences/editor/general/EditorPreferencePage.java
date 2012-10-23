/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.general;

import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import static com.google.eclipse.protobuf.ui.preferences.editor.general.Messages.header;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * General editor preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  @Override protected Control createContents(Composite parent) {
    Composite contents = new Composite(parent, NONE);
    contents.setLayout(new GridLayout(1, false));

    Link link = new Link(contents, SWT.NONE);
    GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    gridData.widthHint = 150; // only expand further if anyone else requires it
    link.setLayoutData(gridData);
    link.setText(header);
    link.addListener(SWT.Selection, new Listener() {
      @Override public void handleEvent(Event event) {
        String text = event.text;
        createPreferenceDialogOn(getShell(), text, null, null);
      }
    });
    return contents;
  }

  @Override public void init(IWorkbench workbench) {}
}

/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor;

import static com.google.eclipse.protobuf.ui.preferences.pages.editor.Messages.header;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

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
    GridData gridData= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    gridData.widthHint= 150; // only expand further if anyone else requires it
    link.setLayoutData(gridData);
    link.setText(header);
    link.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        String u = event.text;
        createPreferenceDialogOn(getShell(), u, null, null);
      }
    });
    return contents;
  }

  public void init(IWorkbench workbench) {}
}

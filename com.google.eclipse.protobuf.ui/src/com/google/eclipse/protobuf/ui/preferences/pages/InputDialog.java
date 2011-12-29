/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Base class for dialogs that accept user input.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class InputDialog extends Dialog {
  private final String title;

  /**
   * Creates a new </code>{@link InputDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param title the title of the dialog.
   */
  public InputDialog(Shell parent, String title) {
    super(parent);
    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    this.title = title;
  }

  @Override protected void configureShell(Shell shell) {
    super.configureShell(shell);
    if (title != null) {
      shell.setText(title);
    }
  }
}

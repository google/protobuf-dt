/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag;

import static com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag.Messages.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.swt.layout.GridData.*;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.regex.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.InputDialog;

/**
 * Dialog where users can enter a new pattern or edit an existing one.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class AddOrEditPatternDialog extends InputDialog {

  private Text txtPattern;
  private Text txtTest;
  private Text txtPatternError;
  private Text txtTestError;

  private String pattern;

  public static AddOrEditPatternDialog editPattern(String pattern, Shell parent) {
    AddOrEditPatternDialog dialog = new AddOrEditPatternDialog(parent, editPattern);
    dialog.pattern = pattern;
    return dialog;
  }

  public static AddOrEditPatternDialog addPattern(Shell parent) {
    return new AddOrEditPatternDialog(parent, addNewPattern);
  }

  public AddOrEditPatternDialog(Shell parent, String title) {
    super(parent, title);
  }

  /** {@inheritDoc} */
  @Override protected Control createDialogArea(Composite parent) {
    Composite cmpDialogArea = (Composite) super.createDialogArea(parent);

    Label lblPattern = new Label(cmpDialogArea, SWT.NONE);
    lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    lblPattern.setText(Messages.pattern);

    txtPattern = new Text(cmpDialogArea, SWT.BORDER);
    txtPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    if (!isEmpty(pattern)) {
      txtPattern.setText(pattern);
      txtPattern.selectAll();
    }

    txtPatternError = new Text(cmpDialogArea, SWT.READ_ONLY | SWT.WRAP);
    txtPatternError.setLayoutData(new GridData(GRAB_HORIZONTAL | HORIZONTAL_ALIGN_FILL));
    Color readOnlyColor = txtPatternError.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    txtPatternError.setBackground(readOnlyColor);

    Label lblSeparator = new Label(cmpDialogArea, SWT.SEPARATOR | SWT.HORIZONTAL);
    lblSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Label lblTest = new Label(cmpDialogArea, SWT.NONE);
    lblTest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    lblTest.setText(testPattern);

    txtTest = new Text(cmpDialogArea, SWT.BORDER);
    txtTest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    txtTestError = new Text(cmpDialogArea, SWT.READ_ONLY | SWT.WRAP);
    GridData gd_txtTestError = new GridData(GRAB_HORIZONTAL | HORIZONTAL_ALIGN_FILL);
    gd_txtTestError.verticalAlignment = SWT.FILL;
    txtTestError.setLayoutData(gd_txtTestError);
    txtTestError.setBackground(readOnlyColor);

    addEventListeners();

    applyDialogFont(cmpDialogArea);
    return cmpDialogArea;
  }

  private void addEventListeners() {
    txtPattern.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        String regex = enteredPattern();
        if (isEmpty(regex)) {
          clearTestErrorText();
          okButton().setEnabled(false);
          return;
        }
        try {
          Pattern.compile(regex, CASE_INSENSITIVE);
        } catch (PatternSyntaxException error) {
          txtPatternError.setText(error.getMessage());
          clearTestErrorText();
          okButton().setEnabled(false);
          return;
        }
        testPattern();
        okButton().setEnabled(true);
      }
    });
    txtTest.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        testPattern();
      }
    });
  }

  private void testPattern() {
    String regex = enteredPattern();
    String testText = txtTest.getText().trim();
    if (isEmpty(regex) || isEmpty(testText)) {
      clearTestErrorText();
      return;
    }
    Pattern p = Pattern.compile(regex, CASE_INSENSITIVE);
    Matcher matcher = p.matcher(testText);
    String result = matcher.matches() ? match : noMatch;
    txtTestError.setText(result);
  }

  private String enteredPattern() {
    return txtPattern.getText().trim();
  }

  public String pattern() {
    return pattern;
  }

  /** {@inheritDoc} */
  @Override protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    okButton().setEnabled(!isEmpty(pattern));
    txtPattern.setFocus();
  }

  private Button okButton() {
    return getButton(OK_ID);
  }

  private void clearTestErrorText() {
    txtTestError.setText(""); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override protected void okPressed() {
    pattern = enteredPattern();
    super.okPressed();
  }
}

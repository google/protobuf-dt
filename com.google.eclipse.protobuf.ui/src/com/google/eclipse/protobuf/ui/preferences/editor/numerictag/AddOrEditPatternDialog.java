/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.numerictag;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.swt.layout.GridData.GRAB_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.addNewPattern;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.editPattern;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.match;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.noMatch;
import static com.google.eclipse.protobuf.ui.preferences.editor.numerictag.Messages.testPattern;
import static com.google.eclipse.protobuf.ui.preferences.pages.SystemColors.widgetBackgroundColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.eclipse.protobuf.ui.preferences.pages.InputDialog;

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
    txtPatternError.setBackground(widgetBackgroundColor());

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
    txtTestError.setBackground(widgetBackgroundColor());

    addEventListeners();

    applyDialogFont(cmpDialogArea);
    return cmpDialogArea;
  }

  private void addEventListeners() {
    txtPattern.addModifyListener(new ModifyListener() {
      @Override public void modifyText(ModifyEvent e) {
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
      @Override public void modifyText(ModifyEvent e) {
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
    txtTestError.setText("");
  }

  /** {@inheritDoc} */
  @Override protected void okPressed() {
    pattern = enteredPattern();
    super.okPressed();
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.Collection;

import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.util.DirectoryNameValidator;

/**
 * Editor where users can add/remove the directories to be used for URI resolution.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryNamesEditor extends Composite {

  private final DirectoryNameValidator directoryNameValidator;

  private List lstDirectoryNames;
  private final Button btnAdd;
  private final Button btnRemove;
  private final Button btnUp;
  private final Button btnDown;

  private SelectionListener onRemoveListener;

  /**
   * Creates a new </code>{@link DirectoryNamesEditor}</code>.
   * @param parent a widget which will be the parent of the new instance (cannot be {@code null}.)
   * @param directoryNameValidator validates that a {@code String} is a valid directory name.
   */
  public DirectoryNamesEditor(Composite parent, DirectoryNameValidator directoryNameValidator) {
    super(parent, SWT.NONE);
    this.directoryNameValidator = directoryNameValidator;
    setLayout(new GridLayout(3, false));

    lstDirectoryNames = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    lstDirectoryNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    composite.setLayout(new GridLayout(1, false));

    btnAdd = new Button(composite, SWT.NONE);
    btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnAdd.setText(add);

    btnRemove = new Button(composite, SWT.NONE);
    btnRemove.setEnabled(false);
    btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnRemove.setText(remove);

    btnUp = new Button(composite, SWT.NONE);
    btnUp.setEnabled(false);
    btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnUp.setText(up);

    btnDown = new Button(composite, SWT.NONE);
    btnDown.setEnabled(false);
    btnDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnDown.setText(down);

    addEventListeners();
  }

  private void addEventListeners() {
    lstDirectoryNames.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        lstDirectoryNames = null;
      }
    });
    lstDirectoryNames.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableButtonsDependingOnListSelection();
      }
    });
    btnAdd.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        IInputValidator validator = new IInputValidator() {
          public String isValid(String newText) {
            if (isEmpty(newText)) return Messages.errorEmptyDirectoryName;
            return directoryNameValidator.validateDirectoryName(newText);
          }
        };
        InputDialog input = new InputDialog(getShell(), directoryNameInputTitle, directoryNameInputMessage, null, validator);
        if (input.open() == OK) {
          lstDirectoryNames.add(input.getValue());
        }
      }
    });
    btnRemove.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        int index = lstDirectoryNames.getSelectionIndex();
        if (index < 0) return;
        lstDirectoryNames.remove(index);
        enableButtonsDependingOnListSelection();
      }
    });
    btnUp.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        swap(true);
      }
    });
    btnDown.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        swap(false);
      }
    });
  }

  private void swap(boolean goUp) {
    int index = lstDirectoryNames.getSelectionIndex();
    if (index < 0) return;
    int target = goUp ? index - 1 : index + 1;
    String[] selection = lstDirectoryNames.getSelection();
    lstDirectoryNames.remove(index);
    lstDirectoryNames.add(selection[0], target);
    lstDirectoryNames.setSelection(target);
    enableButtonsDependingOnListSelection();
  }

  /** {@inheritDoc} */
  @Override public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    lstDirectoryNames.setEnabled(enabled);
    btnAdd.setEnabled(enabled);
    if (enabled) {
      enableButtonsDependingOnListSelection();
    } else {
      btnRemove.setEnabled(false);
      btnUp.setEnabled(false);
      btnDown.setEnabled(false);
    }
  }

  private void enableButtonsDependingOnListSelection() {
    int selectionIndex = lstDirectoryNames.getSelectionIndex();
    int size = lstDirectoryNames.getItemCount();
    boolean hasSelection = selectionIndex >= 0;
    btnRemove.setEnabled(hasSelection);
    boolean hasElements = size > 1;
    btnUp.setEnabled(hasElements && selectionIndex > 0);
    btnDown.setEnabled(hasElements && hasSelection && selectionIndex < size - 1);
  }

  public java.util.List<String> directoryNames() {
    return unmodifiableList(asList(lstDirectoryNames.getItems()));
  }

  public void addDirectoryNames(Collection<String> directoryNames) {
    for (String name : directoryNames) lstDirectoryNames.add(name);
  }

  public void onRemove(SelectionListener listener) {
    if (onRemoveListener != null) lstDirectoryNames.removeSelectionListener(onRemoveListener);
    onRemoveListener = listener;
    lstDirectoryNames.addSelectionListener(onRemoveListener);
  }
}

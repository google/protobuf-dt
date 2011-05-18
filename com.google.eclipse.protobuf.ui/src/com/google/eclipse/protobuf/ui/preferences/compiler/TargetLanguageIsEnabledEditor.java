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

/**
 * Cell editor for {@code TargetLanguageOutputDirectory#isEnabled()} values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class TargetLanguageIsEnabledEditor extends EditingSupport {

  TargetLanguageIsEnabledEditor(ColumnViewer viewer) {
    super(viewer);
  }

  @Override protected CellEditor getCellEditor(Object element) {
    return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
  }

  @Override protected boolean canEdit(Object element) {
    return element instanceof TargetLanguagePreference;
  }

  @Override protected Object getValue(Object element) {
    return ((TargetLanguagePreference)element).isEnabled();
  }

  @Override protected void setValue(Object element, Object value) {
    ((TargetLanguagePreference)element).enabled((Boolean)value);
  }
}

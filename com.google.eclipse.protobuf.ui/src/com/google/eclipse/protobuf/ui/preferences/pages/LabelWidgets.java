/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages;

import static com.google.eclipse.protobuf.ui.preferences.pages.SystemColors.getSystemColor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Label;

/**
 * Utility methods related to <code>{@link Label}</code> widgets.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class LabelWidgets {
  /**
   * Enables the given label widget if the given "enabled" state is {@code true}, and disables it otherwise. This method
   * also sets the foreground of the widget to the system color with id <code>{@link SWT#COLOR_WIDGET_FOREGROUND}</code>
   * if the given "enabled" state is {@code true}, otherwise it sets the foreground of the widget to the system color
   * with id <code>{@link SWT#COLOR_TITLE_INACTIVE_FOREGROUND}</code>.
   * @param label the given label widget.
   * @param enabled the new "enabled" state.
   * @throws SWTException if the given label widget has been disposed or if this method is not called from the thread
   * that created the label widget.
   */
  public static void setEnabled(Label label, boolean enabled) {
    label.setEnabled(enabled);
    updateForegroundColor(label);
  }

  private static void updateForegroundColor(Label label) {
    int colorId = label.isEnabled() ? SWT.COLOR_WIDGET_FOREGROUND : SWT.COLOR_TITLE_INACTIVE_FOREGROUND;
    label.setForeground(getSystemColor(colorId));
  }

  private LabelWidgets() {}
}

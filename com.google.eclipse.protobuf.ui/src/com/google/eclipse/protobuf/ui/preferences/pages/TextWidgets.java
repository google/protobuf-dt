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
import org.eclipse.swt.widgets.Text;

/**
 * Utility methods related to <code>{@link Text}</code> widgets.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class TextWidgets {
  /**
   * Sets the "editable" state of the given text widget. This method also sets the background of the widget to
   * the system color with id <code>{@link SWT#COLOR_LIST_BACKGROUND}</code> if the given "editable" state is
   * {@code true}, otherwise it sets the background of the widget to the system color with id
   * <code>{@link SWT#COLOR_WIDGET_BACKGROUND}</code>.
   * @param text the given text widget.
   * @param editable the new "editable" state.
   * @throws SWTException if the given text widget has been disposed or if this method is not called from the thread
   * that created the text widget.
   */
  public static void setEditable(Text text, boolean editable) {
    text.setEditable(editable);
    updateBackgroundColor(text);
  }

  /**
   * Enables the given text widget if the given "enabled" state is {@code true}, and disables it otherwise. This method
   * also sets the background of the widget to the system color with id <code>{@link SWT#COLOR_LIST_BACKGROUND}</code>
   * if the given "enabled" state is {@code true}, otherwise it sets the background of the widget to the system color
   * with id <code>{@link SWT#COLOR_WIDGET_BACKGROUND}</code>.
   * @param text the given text widget.
   * @param enabled the new "enabled" state.
   * @throws SWTException if the given text widget has been disposed or if this method is not called from the thread
   * that created the text widget.
   */
  public static void setEnabled(Text text, boolean enabled) {
    text.setEnabled(enabled);
    updateBackgroundColor(text);
  }

  private static void updateBackgroundColor(Text text) {
    int colorId = (text.isEnabled() && text.getEditable()) ? SWT.COLOR_LIST_BACKGROUND : SWT.COLOR_WIDGET_BACKGROUND;
    text.setBackground(getSystemColor(colorId));
  }

  private TextWidgets() {}
}

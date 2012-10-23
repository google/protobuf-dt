/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Utility methods related to system colors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class SystemColors {
  /**
   * Returns the system color with id <code>{@link SWT#COLOR_WIDGET_BACKGROUND}</code>.
   * @return the system color with id {@code COLOR_WIDGET_BACKGROUND}.
   */
  public static Color widgetBackgroundColor() {
    return getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
  }

  /**
   * Returns the matching standard color for the given constant, which should one of the color constants specified in
   * <code>{@link SWT}</code>. Any value other than one of the SWT color constants which is passed in will result in the
   * color black. This color should not be free'd because it was allocated by the system, not the application.
   * @param colorId the color constant.
   * @return the matching color.
   * @throws SWTException if this method is not called from the thread that created the receiver or if the receiver has
   * been disposed.
   */
  public static Color getSystemColor(int colorId) {
    return Display.getCurrent().getSystemColor(colorId);
  }

  private SystemColors() {
  }
}

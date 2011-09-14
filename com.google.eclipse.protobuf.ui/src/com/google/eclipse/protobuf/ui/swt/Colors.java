/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Utility methods related to colors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Colors {

  public static Color widgetBackground() {
    return getColor(SWT.COLOR_WIDGET_BACKGROUND);
  }

  public static Color getColor(int systemColorID) {
    return Display.getCurrent().getSystemColor(systemColorID);
  }

  private Colors() {}
}

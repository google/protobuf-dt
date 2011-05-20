/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods related to <code>{@link Shell}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Shells {

  public static void centerShell(Shell target, Shell parent) {
    Rectangle parentRect = parent.getBounds();
    Rectangle targetRect = target.getBounds();
    int x = parentRect.x + (parentRect.width - targetRect.width) / 2;
    int y = parentRect.y + (parentRect.height - targetRect.height) / 2;
    target.setBounds(x, y, targetRect.width, targetRect.height);
  }

}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Utility methods related to Eclipse workbenches.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Workbenches {

  /**
   * Returns the active workbench page.
   * @return the active workbench page, or {@code null} if none can be found.
   */
  public static IWorkbenchPage activeWorkbenchPage() {
    IWorkbenchWindow window = activeWorkbenchWindow();
    return (window == null) ? null : window.getActivePage();
  }

  /**
   * Returns the active workbench window.
   * @return the active workbench window, or {@code null} if none can be found.
   */
  public static IWorkbenchWindow activeWorkbenchWindow() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    return (workbench == null) ? null : workbench.getActiveWorkbenchWindow();
  }

  private Workbenches() {}
}

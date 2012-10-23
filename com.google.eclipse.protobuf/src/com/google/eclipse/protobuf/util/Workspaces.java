/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Utility methods related to workspaces.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Workspaces {
  /**
   * Returns the root of the workspace.
   * @return the root of the workspace.
   */
  public static IWorkspaceRoot workspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }

  private Workspaces() {}
}

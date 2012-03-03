/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swtbot;

import static com.google.eclipse.protobuf.ui.util.Workspaces.workspaceRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Workbench {
  private static final NullProgressMonitor NULL_MONITOR = new NullProgressMonitor();

  private final SWTWorkbenchBot robot;

  public Workbench(SWTWorkbenchBot robot) {
    this.robot = robot;
  }

  public void initialize() throws CoreException {
    robot.closeAllShells();
    closeWelcomeView();
    deleteAllProjects();
  }

  private void closeWelcomeView() {
    try {
    robot.viewByTitle("Welcome").close();
    } catch (WidgetNotFoundException ignored) {}
  }

  private void deleteAllProjects() throws CoreException {
    for (IProject project : workspaceRoot().getProjects()) {
      project.delete(true, true, NULL_MONITOR);
    }
  }
}

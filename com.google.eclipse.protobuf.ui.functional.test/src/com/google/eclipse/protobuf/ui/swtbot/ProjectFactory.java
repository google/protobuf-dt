/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swtbot;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProjectFactory {
  private final SWTWorkbenchBot robot;

  public ProjectFactory(SWTWorkbenchBot robot) {
    this.robot = robot;
  }

  public void createGeneralProject(String name) {
    robot.menu("File").menu("New").menu("Project...").click();
    SWTBotShell shell = robot.shell("New Project");
    shell.activate();
    robot.tree().expandNode("General").select("Project");
    robot.button("Next >").click();
    robot.textWithLabel("Project name:").setText(name);
    robot.button("Finish").click();
  }
}

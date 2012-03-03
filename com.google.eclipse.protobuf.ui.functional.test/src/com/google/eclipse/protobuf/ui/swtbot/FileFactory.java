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
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileFactory {

  private final SWTWorkbenchBot robot;

  public FileFactory(SWTWorkbenchBot robot) {
    this.robot = robot;
  }

  public SWTBotEclipseEditor createFile(String name) {
    robot.menu("File").menu("New").menu("File").click();
    SWTBotShell shell = robot.shell("New File");
    shell.activate();
    robot.textWithLabel("File name:").setText(name);
    robot.button("Finish").click();
    return robot.editorByTitle(name).toTextEditor();
  }
}

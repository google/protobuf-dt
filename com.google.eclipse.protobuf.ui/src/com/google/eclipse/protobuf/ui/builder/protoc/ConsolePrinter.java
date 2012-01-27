/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchPage;
import static org.eclipse.ui.console.IConsoleConstants.ID_CONSOLE_VIEW;

import org.eclipse.ui.*;
import org.eclipse.ui.console.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ConsolePrinter {
  private static final String CONSOLE_NAME = "protoc";

  private final MessageConsoleStream out;

  static ConsolePrinter createAndDisplayConsole() throws PartInitException {
    MessageConsole console = findConsole();
    IWorkbenchPage page = activeWorkbenchPage();
    if (page != null) {
      IConsoleView view = (IConsoleView) page.showView(ID_CONSOLE_VIEW);
      view.display(console);
    }
    return new ConsolePrinter(console);
  }

  private static MessageConsole findConsole() {
    IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    for (IConsole console : consoleManager.getConsoles()) {
      if (CONSOLE_NAME.equals(console.getName()) && console instanceof MessageConsole) {
        return (MessageConsole) console;
      }
    }
    MessageConsole console = new MessageConsole(CONSOLE_NAME, null);
    consoleManager.addConsoles(new IConsole[] { console });
    return console;
  }

  private ConsolePrinter(MessageConsole console) {
    out = console.newMessageStream();
  }

  void printSignal(String s) {
    out.println("[command] " + s);
  }

  void printOutput(String s) {
    out.println("[protoc]  " + s);
  }

  void close() {
    closeQuietly(out);
  }
}

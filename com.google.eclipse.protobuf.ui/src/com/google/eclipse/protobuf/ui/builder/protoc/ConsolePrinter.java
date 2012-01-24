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
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.ui.console.IConsoleConstants.ID_CONSOLE_VIEW;

import org.eclipse.core.runtime.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;
import org.eclipse.ui.progress.UIJob;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ConsolePrinter {
  private static final String CONSOLE_NAME = "protoc";

  private MessageConsoleStream signalStream;
  private MessageConsoleStream outputStream;

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
    signalStream = console.newMessageStream();
    outputStream = console.newMessageStream();
    UIJob job = new UIJob("Set colors in protoc console") {
      @Override public IStatus runInUIThread(IProgressMonitor monitor) {
        Display display = getDisplay();
        signalStream.setColor(new Color(display, 0, 0, 255));
        outputStream.setColor(new Color(display, 255, 0, 0));
        return OK_STATUS;
      }
    };
    job.schedule();
  }

  void printSignal(String s) {
    signalStream.println(s);
  }

  void printOutput(String s) {
    outputStream.println(s);
  }

  void close() {
    close(signalStream);
    close(outputStream);
  }

  private static void close(MessageConsoleStream stream) {
    Color color = stream.getColor();
    if (color != null) {
      color.dispose();
    }
    closeQuietly(stream);
  }
}

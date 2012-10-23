/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static org.eclipse.swtbot.swt.finder.keyboard.Keystrokes.F3;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.ui.junit.CommentReaderRule;
import com.google.eclipse.protobuf.ui.swtbot.ProtobufBot;

/**
 * Tests for "import" hyperlinking.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportHyperlinking_Test {
  private static ProtobufBot robot;

  @BeforeClass public static void setUpOnce() throws CoreException {
    robot = new ProtobufBot();
    robot.resetAll();
    robot.createGeneralProject("ImportHyperlinkingTest");
  }

  @Rule public CommentReaderRule commentReader = new CommentReaderRule();

  // import 'google/protobuf/descriptor.proto';
  @Test public void should_open_file_in_plugIn() throws InterruptedException {
    String text = commentReader.comments().get(0);
    SWTBotEclipseEditor editor = robot.createFileWithText("importDescriptor.proto", text);
    navigateToImportedFile(editor);
    robot.editorByTitle("descriptor.proto");
  }

  private void navigateToImportedFile(SWTBotEclipseEditor editor) {
    editor.navigateTo(0, 10);
    // for "F3" to work on Mac OS, go to "System Preferences" > "Keyboard" and ensure
    // "Use all F1, F2, etc keys as standard function keys" is checked
    editor.pressShortcut(F3);
  }
}

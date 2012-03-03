/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.junit.*;

import com.google.eclipse.protobuf.formatting.ProtobufFormatter;
import com.google.eclipse.protobuf.ui.junit.core.CommentReaderRule;
import com.google.eclipse.protobuf.ui.swtbot.*;

/**
 * Tests for <code>{@link ProtobufFormatter}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Formatter_Test {
  private static SWTWorkbenchBot robot;
  private static FileFactory fileFactory;

  public @Rule CommentReaderRule commentReader = new CommentReaderRule();

  @BeforeClass public static void setUpOnce() throws Exception {
    robot = new SWTWorkbenchBot();
    Workbench workbench = new Workbench(robot);
    workbench.initialize();
    ProjectFactory projectFactory = new ProjectFactory(robot);
    projectFactory.createGeneralProject("FormatterTest");
    fileFactory = new FileFactory(robot);
    SWTBotEclipseEditor editor = fileFactory.createFile("dummy.proto");
    editor.setText("syntax = 'proto2';");
    editor.saveAndClose();
  }

  // import 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_add_line_wrap_after_normal_import() throws Exception {
    SWTBotEclipseEditor editor = fileFactory.createFile("formatNormalImport.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // import public 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import public 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_add_line_wrap_after_public_import() throws Exception {
    SWTBotEclipseEditor editor = fileFactory.createFile("formatPublicImport.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  private Comments commentsAbove() {
    return new Comments(commentReader.commentsInCurrentTestMethod());
  }

  private void formatAndSave(SWTBotEclipseEditor editor) {
    editor.pressShortcut(SWT.MOD1 | SWT.SHIFT, 'F');
    editor.save();
  }

  @After public void tearDown() {
    robot.saveAllEditors();
    robot.closeAllEditors();
  }

  private static class Comments {
    String beforeFormatting;
    String expected;

    Comments(List<String> comments) {
      beforeFormatting = comments.get(0);
      expected = comments.get(1);
    }
  }
}

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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.junit.*;

import com.google.eclipse.protobuf.formatting.ProtobufFormatter;
import com.google.eclipse.protobuf.ui.junit.core.CommentReaderRule;
import com.google.eclipse.protobuf.ui.swtbot.ProtobufBot;

/**
 * Tests for <code>{@link ProtobufFormatter}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Formatter_Test {
  private static ProtobufBot robot;

  public @Rule CommentReaderRule commentReader = new CommentReaderRule();

  @BeforeClass public static void setUpOnce() throws CoreException {
    robot = new ProtobufBot();
    robot.resetAll();
    robot.createGeneralProject("FormatterTest");
    SWTBotEclipseEditor editor = robot.createFile("dummy.proto");
    editor.setText("syntax = 'proto2';");
    editor.saveAndClose();
  }

  // syntax = 'proto2';import 'google/protobuf/descriptor.proto';

  // syntax = 'proto2';
  //
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_syntax() {
    SWTBotEclipseEditor editor = robot.createFile("formatSyntax.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // package com.google.proto.test;import 'google/protobuf/descriptor.proto';

  // package com.google.proto.test;
  //
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_package() {
    SWTBotEclipseEditor editor = robot.createFile("formatPackage.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // import 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_normal_import() {
    SWTBotEclipseEditor editor = robot.createFile("formatNormalImport.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // import public 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import public 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_public_import() {
    SWTBotEclipseEditor editor = robot.createFile("formatPublicImport.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // import weak 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import weak 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_weak_import() {
    SWTBotEclipseEditor editor = robot.createFile("formatWeakImport.proto");
    Comments comments = commentsAbove();
    editor.setText(comments.beforeFormatting);
    formatAndSave(editor);
    assertThat(editor.getText(), equalTo(comments.expected));
  }

  // option java_package = "com.foo.bar";option optimize_for = CODE_SIZE;

  // option java_package = "com.foo.bar";
  // option optimize_for = CODE_SIZE;
  @Test public void should_format_native_option() {
    SWTBotEclipseEditor editor = robot.createFile("formatNativeOption.proto");
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
    robot.saveAndCloseAllEditors();
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

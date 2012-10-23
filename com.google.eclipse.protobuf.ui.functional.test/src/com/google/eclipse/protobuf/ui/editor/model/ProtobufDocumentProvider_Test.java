/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static org.junit.Assert.assertEquals;

import static com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsWritablePreferences.RemoveTrailingWhitespace.ALL_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsWritablePreferences.RemoveTrailingWhitespace.EDITED_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsWritablePreferences.RemoveTrailingWhitespace.NONE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.ui.junit.XtextRule;
import com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsWritablePreferences;
import com.google.eclipse.protobuf.ui.swtbot.ProtobufBot;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufDocumentProvider}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider_Test {
  private static ProtobufBot robot;
  private static SWTBotEclipseEditor editor;

  @BeforeClass public static void setUpOnce() throws CoreException {
    robot = new ProtobufBot();
    robot.resetAll();
    robot.createGeneralProject("ProtobufDocumentProvider");
    editor = robot.createFile("test.proto");
  }

  @Rule public XtextRule xtext = new XtextRule();

  @Inject private SaveActionsWritablePreferences preferences;

  @Test public void should_remove_trailing_whitespace_in_edited_lines_only() {
    initEditor();
    preferences.removeTrailingWhitespace(EDITED_LINES);
    editor.typeText("option optimize_for = SPEED;  ");
    editor.save();
    MultiLineTextBuilder expected = new MultiLineTextBuilder();
    expected.append("syntax = 'proto2';  ")
            .append("import 'google/protobuf/descriptor.proto';  ")
            .append("option optimize_for = SPEED;");
    assertEquals(expected.toString(), editor.getText());
  }

  @Test public void should_remove_trailing_whitespace_in_all_lines() {
    initEditor();
    preferences.removeTrailingWhitespace(ALL_LINES);
    editor.typeText("option optimize_for = SPEED;  ");
    editor.save();
    MultiLineTextBuilder expected = new MultiLineTextBuilder();
    expected.append("syntax = 'proto2';")
            .append("import 'google/protobuf/descriptor.proto';")
            .append("option optimize_for = SPEED;");
    assertEquals(expected.toString(), editor.getText());
  }

  private void initEditor() {
    preferences.removeTrailingWhitespace(NONE);
    MultiLineTextBuilder text = new MultiLineTextBuilder();
    text.append("syntax = 'proto2';  ")
        .append("import 'google/protobuf/descriptor.proto';  ")
        .append("");
    editor.setText(text.toString());
    editor.save();
    editor.navigateTo(2, 0);
  }
}

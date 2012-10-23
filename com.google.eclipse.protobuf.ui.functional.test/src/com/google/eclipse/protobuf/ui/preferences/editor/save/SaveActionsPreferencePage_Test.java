/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.eclipse.protobuf.ui.preferences.AbsractPreferencePageTestCase;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link SaveActionsPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferencePage_Test extends AbsractPreferencePageTestCase {
  @BeforeClass public static void setUpOnce() {
    robot.openPreferencePage("com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsPreferencePage");
  }

  @Inject private SaveActionsPreferences preferences;

  @Before public void setUp() {
    assertTrue(preferences.shouldRemoveTrailingWhitespace());
    assertTrue(preferences.shouldRemoveTrailingWhitespaceInEditedLines());
  }

  @Test public void should_update_property_for_removing_trailing_whitespace() {
    robot.checkBox("Remove trailing whitespace").deselect();
    applyChanges();
    assertFalse(preferences.shouldRemoveTrailingWhitespace());
  }

  @Test public void should_update_property_for_removing_trailing_whitespace_in_edited_lines() {
    robot.radio("In all lines").click();
    applyChanges();
    assertFalse(preferences.shouldRemoveTrailingWhitespaceInEditedLines());
  }
}

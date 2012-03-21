/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;

import org.eclipse.core.runtime.CoreException;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.eclipse.protobuf.ui.swtbot.ProtobufBot;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class AbsractPreferencePageTestCase {
  protected static ProtobufBot robot;

  @BeforeClass public static void resetWorkbench() throws CoreException {
    robot = new ProtobufBot();
    robot.resetAll();
  }

  @Rule public XtextRule xtext = createWith(ProtobufEditorPlugIn.injector());

  @Before public void restoreDefaults() {
    robot.button("Restore Defaults").click();
    applyChanges();
  }

  protected void applyChanges() {
    robot.button("Apply").click();
  }
}

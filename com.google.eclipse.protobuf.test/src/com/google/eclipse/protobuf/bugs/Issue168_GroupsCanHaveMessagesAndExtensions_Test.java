/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=168">Issue 168</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue168_GroupsCanHaveMessagesAndExtensions_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message TopMessage {
  //   message MidMessage {
  //     message BottomMessage {
  //       extensions 4 to max;
  //     }
  //
  //     optional group BottomGroup = 1 {
  //      extensions 4 to max;
  //     }
  //   }
  //
  //   optional group MidGroup = 2 {
  //     message BottomMessage {
  //       extensions 4 to max;
  //     }
  //
  //     optional group BottomGroup = 3 {
  //       extensions 4 to max;
  //     }
  //   }
  // }
  @Test public void should_recognize_messages_and_extensions_in_groups() {
    // should not find syntax errors
  }
}

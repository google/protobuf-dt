/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.junit.Assert.assertNotNull;

import com.google.eclipse.protobuf.junit.core.XtextRule;

import org.junit.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=150">Issue 150</a>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue150_AddSupportForExtendMessageToGroups_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  // message ABC {
  //   extensions 1000 to max;
  // }
  //
  // message XYZ {
  //   optional group MyGroup = 1 {
  //     extend ABC {
  //       optional int32 extension_in_group = 1000;
  //     }
  //   }
  // }
  @Test public void should_recognize_extendMessage_in_group() {
    assertNotNull(xtext.root());
  }
}

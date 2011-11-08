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

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=153">Issue 153</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue153_AddSupportForNegativeInf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  // syntax = "proto2";
  //
  // message Foo {
  //   optional double bar = 1 [default = -inf];
  // }
  @Test public void should_recognize_negative_inf() {
    assertNotNull(xtext.root());
  }
}

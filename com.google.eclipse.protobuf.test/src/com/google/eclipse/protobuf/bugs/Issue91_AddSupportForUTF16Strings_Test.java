/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertNotNull;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=91">Issue 91</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue91_AddSupportForUTF16Strings_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message Foo {
  //   optional string bar = 1 [default="\\302\\265"];
  // }
  @Test public void should_recognize_UTF16_strings() {
    assertNotNull(xtext.root());
  }
}

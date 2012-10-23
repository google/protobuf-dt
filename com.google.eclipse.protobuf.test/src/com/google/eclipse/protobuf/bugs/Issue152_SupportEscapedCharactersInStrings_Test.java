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
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=152">Issue 152</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue152_SupportEscapedCharactersInStrings_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message Foo {
  //   optional bytes escaped_bytes = 1 [default = "\0\001\a\b\f\n\r\t\v\\\'\"\xfe"];
  // }
  @Test public void should_recognize_escaped_characters() {
    assertNotNull(xtext.root());
  }
}

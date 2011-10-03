/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;

import org.junit.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=91">Issue 91</a>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue91AddSupportForUTF16Strings {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();

  @Test public void should_recognize_UTF16_strings() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Foo {                                      ")
         .append("  optional string bar = 1 [default=\"\\302\\265\"];")
         .append("}                                                  ");
    xtext.parseText(proto);
  }
}

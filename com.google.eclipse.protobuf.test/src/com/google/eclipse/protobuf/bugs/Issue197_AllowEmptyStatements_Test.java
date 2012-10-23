/*
 * Copyright (c) 2012 Google Inc.
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
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Message;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=197">Issue 197</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue197_AllowEmptyStatements_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";;
  //
  // message Person {}
  @Test public void should_allow_empty_statement_in_syntax() {
    xtext.find("Person", Message.class);
  }

  // syntax = "proto2";
  //
  // package com.google.proto.test;;
  //
  // message Person {}
  @Test public void should_allow_empty_statement_in_package() {
    xtext.find("Person", Message.class);
  }

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";;
  //
  // message Person {}
  @Test public void should_allow_empty_statement_in_import() {
    xtext.find("Person", Message.class);
  }

  // syntax = "proto2";
  //
  // option optimize_for = CODE_SIZE;;
  //
  // message Person {}
  @Test public void should_allow_empty_statement_in_option() {
    xtext.find("Person", Message.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   extensions 1000 to max;;
  // }
  //
  // message PhoneNumber {}
  @Test public void should_allow_empty_statement_in_extension() {
    xtext.find("PhoneNumber", Message.class);
  }

  // syntax = "proto2";
  //
  // enum Type {
  //   HOME = 0;;
  //   OFFICE = 1;
  // }
  @Test public void should_allow_empty_statement_in_literal() {
    xtext.find("OFFICE", Literal.class);
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // service Service {
  //   rpc Rpc (Person) returns (Person);;
  // }
  //
  // message PhoneNumber {}
  @Test public void should_allow_empty_statement_in_rpc() {
    xtext.find("PhoneNumber", Message.class);
  }
}

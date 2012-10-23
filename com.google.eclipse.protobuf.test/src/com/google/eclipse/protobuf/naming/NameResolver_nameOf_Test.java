/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.ecore.EObject;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Service;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link NameResolver#nameOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NameResolver_nameOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private NameResolver resolver;

  // syntax = "proto2";
  //
  // package com.google.proto.test;
  @Test public void should_return_name_of_Package() {
    Package aPackage = xtext.find("com.google.proto.test", Package.class);
    String name = resolver.nameOf(aPackage);
    assertThat(name, equalTo("com.google.proto.test"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  @Test public void should_return_name_of_Message() {
    Message message = xtext.find("Person", Message.class);
    String name = resolver.nameOf(message);
    assertThat(name, equalTo("Person"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional String firstName = 1;
  // }
  @Test public void should_return_name_of_MessageField() {
    MessageField field = xtext.find("firstName", MessageField.class);
    String name = resolver.nameOf(field);
    assertThat(name, equalTo("firstName"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional group Names = 1 {};
  // }
  @Test public void should_return_name_of_Group() {
    Group group = xtext.find("Names", Group.class);
    String name = resolver.nameOf(group);
    assertThat(name, equalTo("Names"));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {}
  @Test public void should_return_name_of_Enum() {
    Enum anEnum = xtext.find("PhoneType", Enum.class);
    String name = resolver.nameOf(anEnum);
    assertThat(name, equalTo("PhoneType"));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   HOME = 0;
  // }
  @Test public void should_return_name_of_Literal() {
    Literal literal = xtext.find("HOME", Literal.class);
    String name = resolver.nameOf(literal);
    assertThat(name, equalTo("HOME"));
  }

  // syntax = "proto2";
  //
  // service CallServer {}
  @Test public void should_return_name_of_Service() {
    Service service = xtext.find("CallServer", Service.class);
    String name = resolver.nameOf(service);
    assertThat(name, equalTo("CallServer"));
  }

  // syntax = "proto2";
  //
  // message Input {}
  // message Output {}
  //
  // service CallServer {
  //   rpc QuickCall (Input) returns (Output);
  // }
  @Test public void should_return_name_of_Rpc() {
    Rpc rpc = xtext.find("QuickCall", Rpc.class);
    String name = resolver.nameOf(rpc);
    assertThat(name, equalTo("QuickCall"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional boolean active = 1 [default = true];
  // }
  @Test public void should_return_name_of_default_value_option() {
    DefaultValueFieldOption option = xtext.find("default", DefaultValueFieldOption.class);
    String name = resolver.nameOf(option);
    assertThat(name, equalTo("default"));
  }
}

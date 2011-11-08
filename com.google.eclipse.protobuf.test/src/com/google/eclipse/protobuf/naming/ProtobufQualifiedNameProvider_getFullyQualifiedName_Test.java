/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.naming.*;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufQualifiedNameProvider}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider_getFullyQualifiedName_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ProtobufQualifiedNameProvider provider;

  @Before public void setUp() {
    provider = (ProtobufQualifiedNameProvider) xtext.getInstanceOf(IQualifiedNameProvider.class);
  }

  // syntax = "proto2";
  //
  // package fqn.test;
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_include_existing_package_name_as_part_of_message_FQN() {
    Message person = xtext.find("Person", Message.class);
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("fqn.test.Person"));
  }

  // syntax = "proto2";
  //
  // package fqn.test;
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_include_existing_package_name_as_part_of_property_FQN() {
    Property name = xtext.find("name", Property.class);
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("fqn.test.Person.name"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_not_include_package_name_as_part_of_message_FQN_if_package_is_not_specified() {
    Message person = xtext.find("Person", Message.class);
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("Person"));
  }

  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_not_include_package_name_as_part_of_property_FQN_if_package_is_not_specified() {
    Property name = xtext.find("name", Property.class);
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("Person.name"));
  }
}

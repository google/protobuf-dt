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
import static com.google.eclipse.protobuf.junit.model.find.MessageFinder.findMessage;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.naming.*;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
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

  @Test public void should_include_existing_package_name_as_part_of_message_FQN() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package fqn.test;          ")
         .append("                           ")
         .append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Message person = findMessage(name("Person"), in(root));
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("fqn.test.Person"));
  }

  @Test public void should_include_existing_package_name_as_part_of_property_FQN() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package fqn.test;          ")
         .append("                           ")
         .append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property name = findProperty(name("name"), in(root));
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("fqn.test.Person.name"));
  }

  @Test public void should_not_include_package_name_as_part_of_message_FQN_if_package_is_not_specified() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Message person = findMessage(name("Person"), in(root));
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("Person"));
  }

  @Test public void should_not_include_package_name_as_part_of_property_FQN_if_package_is_not_specified() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property name = findProperty(name("name"), in(root));
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("Person.name"));
  }
}

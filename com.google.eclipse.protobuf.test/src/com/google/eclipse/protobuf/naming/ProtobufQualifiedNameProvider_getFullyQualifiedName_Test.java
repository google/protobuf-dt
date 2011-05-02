/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.junit.Finder.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufQualifiedNameProvider}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider_getFullyQualifiedName_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private ProtobufQualifiedNameProvider provider;
  
  @Before public void setUp() {
    provider = (ProtobufQualifiedNameProvider) xtext.getInstanceOf(IQualifiedNameProvider.class);
  }
  
  @Test public void should_include_existing_package_name_as_part_of_message_FQN() {
    StringBuilder proto = new StringBuilder();
    proto.append("package fqn.test;          ")
         .append("                           ")
         .append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Message person = findMessage("Person", root);
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("fqn.test.Person"));
  }

  @Test public void should_include_existing_package_name_as_part_of_property_FQN() {
    StringBuilder proto = new StringBuilder();
    proto.append("package fqn.test;          ")
         .append("                           ")
         .append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property name = findProperty("name", root);
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("fqn.test.Person.name"));
  }

  @Test public void should_not_include_package_name_as_part_of_message_FQN_if_package_is_not_specified() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Message person = findMessage("Person", root);
    QualifiedName fqn = provider.getFullyQualifiedName(person);
    assertThat(fqn.toString(), equalTo("Person"));
  }

  @Test public void should_not_include_package_name_as_part_of_property_FQN_if_package_is_not_specified() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property name = findProperty("name", root);
    QualifiedName fqn = provider.getFullyQualifiedName(name);
    assertThat(fqn.toString(), equalTo("Person.name"));
  }
}

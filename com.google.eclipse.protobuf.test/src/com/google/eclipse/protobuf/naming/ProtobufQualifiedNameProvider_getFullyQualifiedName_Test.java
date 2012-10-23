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
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufQualifiedNameProvider#getFullyQualifiedName(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider_getFullyQualifiedName_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IProtobufQualifiedNameProvider provider;

  // syntax = "proto2";
  //
  // package fqn.test;
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_include_existing_package_name_as_part_of_message_FQN() {
    Message message = xtext.find("Person", Message.class);
    QualifiedName fqn = provider.getFullyQualifiedName(message);
    assertThat(fqn.toString(), equalTo("fqn.test.Person"));
  }

  // syntax = "proto2";
  //
  // package fqn.test;
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_include_existing_package_name_as_part_of_field_FQN() {
    MessageField field = xtext.find("name", MessageField.class);
    QualifiedName fqn = provider.getFullyQualifiedName(field);
    assertThat(fqn.toString(), equalTo("fqn.test.Person.name"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_not_include_package_name_as_part_of_message_FQN_if_package_is_not_specified() {
    Message message = xtext.find("Person", Message.class);
    QualifiedName fqn = provider.getFullyQualifiedName(message);
    assertThat(fqn.toString(), equalTo("Person"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_not_include_package_name_as_part_of_field_FQN_if_package_is_not_specified() {
    MessageField field = xtext.find("name", MessageField.class);
    QualifiedName fqn = provider.getFullyQualifiedName(field);
    assertThat(fqn.toString(), equalTo("Person.name"));
  }
}

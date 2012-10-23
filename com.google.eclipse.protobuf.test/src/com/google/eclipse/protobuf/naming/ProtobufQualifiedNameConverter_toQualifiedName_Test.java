/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufQualifiedNameConverter#toQualifiedName(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameConverter_toQualifiedName_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufQualifiedNameConverter converter;

  @Test public void should_create_qualified_name_from_string_without_whitespace_or_line_returns() {
    String s = "com.google.proto.Test";
    QualifiedName fqn = converter.toQualifiedName(s);
    List<String> expectedSegments = newArrayList("com", "google", "proto", "Test");
    assertThat(fqn.getSegments(), equalTo(expectedSegments));
  }

  @Test public void should_create_qualified_name_from_string_with_whitespace() {
    String s = "com.google. proto.  Test";
    QualifiedName fqn = converter.toQualifiedName(s);
    List<String> expectedSegments = newArrayList("com", "google", "proto", "Test");
    assertThat(fqn.getSegments(), equalTo(expectedSegments));
  }

  @Test public void should_create_qualified_name_from_string_with_line_returns() {
    String s = "com.google.\nproto.\n\nTest";
    QualifiedName fqn = converter.toQualifiedName(s);
    List<String> expectedSegments = newArrayList("com", "google", "proto", "Test");
    assertThat(fqn.getSegments(), equalTo(expectedSegments));
  }

  @Test public void should_create_qualified_name_from_string_with_whitespace_and_line_returns() {
    String s = "com.\ngoogle. proto.\n Test";
    QualifiedName fqn = converter.toQualifiedName(s);
    List<String> expectedSegments = newArrayList("com", "google", "proto", "Test");
    assertThat(fqn.getSegments(), equalTo(expectedSegments));
  }
}

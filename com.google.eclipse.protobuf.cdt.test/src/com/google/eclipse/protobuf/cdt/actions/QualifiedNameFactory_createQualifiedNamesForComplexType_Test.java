/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static com.google.eclipse.protobuf.cdt.junit.QualifiedNamesContain.containOnly;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.cdt.ProtobufCdtModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link QualifiedNameFactory#createQualifiedNamesForComplexType(String[])}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class QualifiedNameFactory_createQualifiedNamesForComplexType_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new ProtobufCdtModule());

  @Inject private QualifiedNameFactory qualifiedNameFactory;

  @Test public void should_return_single_qualified_name_for_top_level_type() {
    String[] segments = { "com", "google", "proto", "Test" };
    List<QualifiedName> qualifiedNames = qualifiedNameFactory.createQualifiedNamesForComplexType(segments);
    assertThat(qualifiedNames, containOnly("com.google.proto.Test"));
  }

  @Test public void should_split_in_underscore_for_one_level_nesting() {
    String[] segments = { "com", "google", "proto", "Test_Inner" };
    List<QualifiedName> qualifiedNames = qualifiedNameFactory.createQualifiedNamesForComplexType(segments);
    assertThat(qualifiedNames, containOnly("com.google.proto.Test_Inner", "com.google.proto.Test.Inner"));
  }

  @Test public void should_split_in_underscore_for_multiple_level_nesting() {
    String[] segments = { "com", "google", "proto", "Test_Inner_Inner" };
    List<QualifiedName> qualifiedNames = qualifiedNameFactory.createQualifiedNamesForComplexType(segments);
    assertThat(qualifiedNames, containOnly("com.google.proto.Test_Inner_Inner", "com.google.proto.Test.Inner.Inner"));
  }}

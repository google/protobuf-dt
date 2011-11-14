/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.core.Setups.integrationTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.scoping.ContainAllNames.containAll;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_FieldName_target(FieldName, EReference)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_ExtensionFieldName_target_Test {

  private static EReference reference;

  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private ProtobufScopeProvider provider;

  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  //
  // package google.proto.test;
  //
  // message Aggregate {
  //   optional string s = 1;
  //   optional google.protobuf.FileOptions file = 2;
  // }
  //
  // extend google.protobuf.FileOptions { 
  //   optional Aggregate fileopt = 15478479;
  // }
  //
  // option (fileopt) = {
  //   file {
  //     [google.proto.test.fileopt] {
  //       s:'FileExtensionAnnotation'
  //     }
  //   }
  // };
  @Test public void should_provide_sources_for_aggregate_field() {
    ValueField field = xtext.find("google.proto.test.fileopt", "]", ValueField.class);
    ExtensionFieldName name = (ExtensionFieldName) field.getName();
    IScope scope = provider.scope_FieldName_target(name, reference);
    assertThat(descriptionsIn(scope), containAll("google.proto.test.fileopt", ".google.proto.test.fileopt"));
  }
}

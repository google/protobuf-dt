/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainAllNames.containAll;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ExtensionFieldName;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.ValueField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_FieldName_target(FieldName, EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_FieldName_target_with_ExtensionFieldName_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtobufScopeProvider scopeProvider;

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
    IScope scope = scopeProvider.scope_FieldName_target(name, reference);
    assertThat(descriptionsIn(scope), containAll("google.proto.test.fileopt", ".google.proto.test.fileopt"));
  }
}

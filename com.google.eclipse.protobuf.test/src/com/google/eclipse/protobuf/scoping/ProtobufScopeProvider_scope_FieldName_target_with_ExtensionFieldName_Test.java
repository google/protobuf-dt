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
import static com.google.eclipse.protobuf.junit.matchers.ContainNames.contain;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.FIELD_NAME__TARGET;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.ComplexValueCurlyBracket;
import com.google.eclipse.protobuf.protobuf.ComplexValueField;
import com.google.eclipse.protobuf.protobuf.ExtensionFieldName;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.SimpleValueField;
import com.google.eclipse.protobuf.protobuf.ValueField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_FieldName_target(FieldName,
 * EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_FieldName_target_with_ExtensionFieldName_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

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
    ComplexValueField valueField = xtext.find("file", " {", ComplexValueField.class);
    ComplexValue value = valueField.getValues().get(0);
    ValueField field = value.getFields().get(0);
    ExtensionFieldName name = (ExtensionFieldName) field.getName();
    IScope scope = scopeProvider.getScope(name, FIELD_NAME__TARGET);
    assertThat(descriptionsIn(scope), contain("google.proto.test.fileopt"));
  }

  // // Create file sample_proto.proto
  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  //
  // package google.proto.sample;
  //
  // message Aggregate {
  //   optional string s = 1;
  //   optional google.protobuf.FileOptions file = 2;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Aggregate fileopt = 15478479;
  // }

  // syntax = "proto2";
  //
  // import "sample_proto.proto";
  //
  // package google.proto.test;
  //
  // option (google.proto.sample.fileopt) = {
  //   file {
  //     [google.proto.sample.fileopt] {
  //       s:'FileExtensionAnnotation'
  //     }
  //   }
  // };
  @Test public void should_provide_sources_for_aggregate_field_from_import() {
    Option option = xtext.find("fileopt", ")", Option.class);
    ComplexValueCurlyBracket valueCurlyBracket = (ComplexValueCurlyBracket) option.getValue();
    ComplexValueField file = (ComplexValueField) valueCurlyBracket.getFields().get(0);
    ComplexValueField fileopt = (ComplexValueField) file.getValues().get(0).getFields().get(0);
    SimpleValueField s = (SimpleValueField) fileopt.getValues().get(0).getFields().get(0);
    IScope scope = scopeProvider.getScope(s.getName(), FIELD_NAME__TARGET);
    assertThat(descriptionsIn(scope), contain("s"));
  }
}

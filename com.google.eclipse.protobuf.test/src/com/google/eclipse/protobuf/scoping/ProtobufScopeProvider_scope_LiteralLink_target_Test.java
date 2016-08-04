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
import static org.junit.Assert.assertEquals;
import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainNames.contain;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.LITERAL_LINK__TARGET;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#getScope(LiteralLink, EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_LiteralLink_target_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // message Person {
  //   optional Type type = 1 [default = ONE];
  // }
  @Test public void should_provide_Literals_for_default_value() {
    MessageField field = xtext.find("type", " =", MessageField.class);
    FieldOption option = field.getFieldOptions().get(0);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("ONE", "TWO"));
  }

  // syntax = "proto2";
  //
  // option optimize_for = SPEED;
  @Test public void should_provide_Literals_for_native_option() {
    Option option = xtext.find("optimize_for", Option.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("SPEED", "CODE_SIZE", "LITE_RUNTIME"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //
  // option (type) = ONE;
  @Test public void should_provide_Literals_for_source_of_custom_option() {
    Option option = xtext.find("type", ")", Option.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("ONE", "TWO"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // message Info {
  //   optional Type type = 1;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Info info = 1000;
  // }
  //
  // option (info).type = ONE;
  @Test public void should_provide_Literals_for_source_of_field_of_custom_option() {
    Option option = xtext.find("info", ")", Option.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("ONE", "TWO"));
  }

  private static LiteralLink valueOf(Option option) {
    return (LiteralLink) option.getValue();
  }

  // syntax = "proto2";
  //
  //  message Person {
  //    optional Type type = 1 [ctype = STRING];
  //  }
  @Test public void should_provide_Literals_for_source_of_native_field_option() {
    FieldOption option = xtext.find("ctype", FieldOption.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("STRING", "CORD", "STRING_PIECE"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(type) = ONE];
  // }
  @Test public void should_provide_Literals_for_source_of_custom_field_option() {
    FieldOption option = xtext.find("type", ")", FieldOption.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("ONE", "TWO"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // message Info {
  //   optional Type type = 1;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Info info = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(info).type = ONE];
  // }
  @Test public void should_provide_Literals_for_source_of_field_in_custom_field_option() {
    FieldOption option = xtext.find("info", ")", FieldOption.class);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("ONE", "TWO"));
  }

  // // Create file sample_proto.proto
  // syntax = "proto2";
  // package sample.proto;
  //
  // message Example {
  //   enum Alpha {
  //     BAR =  1;
  //   }
  // }

  //  syntax = "proto2";
  //  package example.proto;
  //  import "sample_proto.proto";
  //
  // message Fruits {
  //  enum Beta {
  //     BAR =  1;
  //  }
  //   optional Grape grape = 4 [default = BAR];
  // }
  @Test public void should_provide_Literal_nearest_to_option() {
    MessageField field = xtext.find("grape", " =", MessageField.class);
    DefaultValueFieldOption option = (DefaultValueFieldOption) field.getFieldOptions().get(0);
    LiteralLink link = (LiteralLink) option.getValue();
    Enum enumBeta = (Enum)link.getTarget().eContainer();
    Enum beta = xtext.find("Beta", Enum.class);
    Literal literal = (Literal) beta.getElements().get(0);
    Enum expectedEnum = (Enum) literal.eContainer();
    assertEquals(expectedEnum, enumBeta);
  }

  // syntax = "proto2";
  //
  // message Status {
  //   enum StatusCode {
  //     SUCCESS = 1;
  //   }
  //   required StatusCode statusCode = 1 [default = SUCCESS];
  // }
  //
  // message Response {
  //   enum Status {
  //     SUCCESS = 1;
  //   }
  //   required Status status = 1 [default = SUCCESS];
  // }
  @Test public void should_provide_Literals_in_nearest_scope_to_default_value_field_option() {
    MessageField field = xtext.find("status", " =", MessageField.class);
    DefaultValueFieldOption option = (DefaultValueFieldOption) field.getFieldOptions().get(0);
    LiteralLink link = (LiteralLink) option.getValue();
    Enum enumActual = (Enum)link.getTarget().eContainer();
    Message message = xtext.find("Response", Message.class);
    Enum status = (Enum) message.getElements().get(0);
    Literal literal = (Literal) status.getElements().get(0);
    Enum expectedEnum = (Enum) literal.eContainer();
    assertEquals(expectedEnum, enumActual);
  }

  // syntax = "proto2";
  //
  // message Status {
  //   enum StatusCode {
  //     SUCCESS = 1;
  //   }
  //   required StatusCode statusCode = 1 [default = SUCCESS];
  // }
  //
  // message Response {
  //   enum Status {
  //     SUCCESS = 1;
  //   }
  //   required Status status = 1 [default = SUCCESS];
  // }
  @Test public void should_provide_Literals_for_source_of_default_value_field_option() {
    MessageField field = xtext.find("status", " =", MessageField.class);
    DefaultValueFieldOption option = (DefaultValueFieldOption) field.getFieldOptions().get(0);
    IScope scope = scopeProvider.getScope(valueOf(option), LITERAL_LINK__TARGET);
    assertThat(descriptionsIn(scope), contain("SUCCESS"));
  }

  private static LiteralLink valueOf(FieldOption option) {
    return (LiteralLink) option.getValue();
  }
}

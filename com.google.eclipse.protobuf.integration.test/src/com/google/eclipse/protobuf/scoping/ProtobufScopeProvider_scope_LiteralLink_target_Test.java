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
import static com.google.eclipse.protobuf.junit.matchers.ContainAllLiteralsInEnum.containAllLiteralsIn;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_LiteralLink_target(LiteralLink, EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_LiteralLink_target_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtoDescriptorProvider descriptorProvider;
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
    FieldOption option = xtext.find("default", FieldOption.class);
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum typeEnum = xtext.find("Type", " {", Enum.class);
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  // syntax = "proto2";
  //
  // option optimize_for = SPEED;
  @Test public void should_provide_Literals_for_native_option() {
    Option option = xtext.find("optimize_for", Option.class);
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum optimizeModeEnum = descriptorProvider.primaryDescriptor().enumByName("OptimizeMode");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(optimizeModeEnum));
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
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum typeEnum = xtext.find("Type", " {", Enum.class);
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
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
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum typeEnum = xtext.find("Type", " {", Enum.class);
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
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
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Enum cTypeEnum = descriptor.enumByName("CType");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(cTypeEnum));
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
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum typeEnum = xtext.find("Type", " {", Enum.class);
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
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
    IScope scope = scopeProvider.scope_LiteralLink_target(valueOf(option), reference);
    Enum typeEnum = xtext.find("Type", " {", Enum.class);
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  private static LiteralLink valueOf(FieldOption option) {
    return (LiteralLink) option.getValue();
  }
}

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
import static com.google.eclipse.protobuf.junit.core.SearchOption.IGNORE_CASE;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainNames.contain;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION_FIELD__TARGET;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.MessageOptionField;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#getScope(OptionField, EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_OptionField_target_with_MessageOptionField_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   optional double code = 1;
  //   optional string name = 2;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //
  // option (type).code = 68;
  @Test public void should_provide_message_fields_for_first_field_in_custom_option() {
    CustomOption option = xtext.find("type", ")", CustomOption.class);
    MessageOptionField codeOptionField = (MessageOptionField) option.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "name"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Code {
  //   optional double number = 1;
  // }
  //
  // message Type {
  //   optional Code code = 1;
  //   optional string name = 2;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //
  // option (type).code.number = 68;
  @Test public void should_provide_message_fields_for_field_in_custom_option() {
    CustomOption option = xtext.find("type", ")", CustomOption.class);
    OptionField numberOptionField = (MessageOptionField) option.getFields().get(1);
    IScope scope = scopeProvider.getScope(numberOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("number"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FileOptions {
  //   optional group Type = 1000 {
  //     optional double code = 1001;
  //     optional string name = 1002;
  //   }
  // }
  //
  // option (type).code = 68;
  @Test public void should_provide_group_fields_for_first_field_in_custom_option() {
    CustomOption option = xtext.find("type", ")", CustomOption.class, IGNORE_CASE);
    MessageOptionField codeOptionField = (MessageOptionField) option.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "name"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   optional double code = 1;
  //   optional string name = 2;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(type).code = 68];
  // }
  @Test public void should_provide_message_fields_for_first_field_in_field_custom_option() {
    CustomFieldOption option = xtext.find("type", ")", CustomFieldOption.class);
    MessageOptionField codeOptionField = (MessageOptionField) option.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "name"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Code {
  //   optional double number = 1;
  // }
  //
  // message Type {
  //   optional Code code = 1;
  //   optional string name = 2;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(type).code.number = 68];
  // }
  @Test public void should_provide_message_fields_for_field_in_field_custom_option() {
    CustomFieldOption option = xtext.find("type", ")", CustomFieldOption.class);
    MessageOptionField numberOptionField = (MessageOptionField) option.getFields().get(1);
    IScope scope = scopeProvider.getScope(numberOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("number"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FieldOptions {
  //   optional group Type = 1000 {
  //     optional double code = 1001;
  //     optional string name = 1002;
  //   }
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(type).code = 68];
  // }
  @Test public void should_provide_group_fields_for_first_field_in_field_custom_option() {
    MessageField messageField = xtext.find("active", MessageField.class);
    EList<FieldOption> fieldOptions = messageField.getFieldOptions();
    CustomFieldOption customFieldOption = (CustomFieldOption) fieldOptions.get(0);
    OptionField codeOptionField = customFieldOption.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "name"));
  }

  // syntax = "proto2";
  //
  // package com.google.proto;
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   bool foo = 1;
  //   oneof choose_one {
  //     bool bar = 2;
  //     bool baz = 3;
  //   }
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional bool active = 1 [(type).bar = true];
  // }
  @Test public void should_provide_field_option_from_oneof() {
    CustomFieldOption option = xtext.find("type", ")", CustomFieldOption.class);
    MessageOptionField codeOptionField = (MessageOptionField) option.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("foo", "bar", "baz"));
  }

  // syntax = "proto2";
  //
  // package com.google.proto;
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   bool foo = 1;
  //   oneof choose_one {
  //     bool bar = 2;
  //     bool baz = 3;
  //   }
  //   oneof choose_another {
  //     bool qux = 4;
  //     bool quux = 5;
  //   }
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional bool active = 1 [(type).bar = true];
  // }
  @Test public void should_provide_field_option_from_multiple_oneof() {
    CustomFieldOption option = xtext.find("type", ")", CustomFieldOption.class);
    MessageOptionField codeOptionField = (MessageOptionField) option.getFields().get(0);
    IScope scope = scopeProvider.getScope(codeOptionField, OPTION_FIELD__TARGET);
    assertThat(descriptionsIn(scope), contain("foo", "bar", "baz", "qux", "quux"));
  }
}

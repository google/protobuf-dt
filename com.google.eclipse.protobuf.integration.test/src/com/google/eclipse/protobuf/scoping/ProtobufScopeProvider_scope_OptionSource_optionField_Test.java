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
import static com.google.eclipse.protobuf.scoping.ContainAllFields.containAll;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.scoping.OptionType.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_OptionSource_optionField(OptionSource, EReference)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_OptionSource_optionField_Test {

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
  // option optimize_for = SPEED;
  @Test public void should_provide_Property_fields_for_native_option() {
    Option option = xtext.find("optimize_for", Option.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    Collection<MessageField> optionSources = descriptor().optionsOfType(FILE);
    assertThat(descriptionsIn(scope), containAll(optionSources));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional Type type = 1 [ctype = STRING];
  // }
  @Test public void should_provide_Property_fields_for_native_field_option() {
    NativeFieldOption option = xtext.find("ctype", NativeFieldOption.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    Collection<MessageField> optionSources = descriptor().optionsOfType(FIELD);
    assertThat(descriptionsIn(scope), containAll(optionSources));
  }

  private ProtoDescriptor descriptor() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    return descriptorProvider.primaryDescriptor();
  }

  // syntax = "proto2";
  //
  // package com.google.proto;
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FileOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1001;
  // }
  //
  // option (code) = 68;
  @Test public void should_provide_Property_fields_for_custom_option() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("code", "proto.code", "google.proto.code", "com.google.proto.code",
                                                 ".com.google.proto.code",
                                                 "info", "proto.info", "google.proto.info", "com.google.proto.info",
                                                 ".com.google.proto.info"));
  }

  // // Create file custom-options.proto
  // syntax = "proto2";
  // package test.proto;
  //
  // import "google/protobuf/descriptor.proto";
  //
  // extend google.protobuf.FileOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1002;
  // }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // import 'custom-options.proto';
  //
  // option (test.proto.code) = 68;
  @Test public void should_provide_imported_Property_fields_for_custom_option() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("test.proto.code", ".test.proto.code",
                                                 "test.proto.info", ".test.proto.info"));
  }

  // // Create file custom-options.proto
  // syntax = "proto2";
  // package com.google.proto;
  //
  // import "google/protobuf/descriptor.proto";
  //
  // extend google.protobuf.FileOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1002;
  // }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // import 'custom-options.proto';
  //
  // option (code) = 68;
  @Test public void should_provide_imported_Property_fields_for_custom_option_with_equal_package() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("code", "proto.code", "google.proto.code", "com.google.proto.code",
                                                 ".com.google.proto.code",
                                                 "info", "proto.info", "google.proto.info", "com.google.proto.info",
                                                 ".com.google.proto.info"));
  }

  // syntax = "proto2";
  // package com.google.proto;
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FieldOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1001;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(code) = 68];
  // }
  @Test public void should_provide_Property_fields_for_custom_field_option() {
    CustomFieldOption option = xtext.find("code", ")", CustomFieldOption.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("code", "proto.code", "google.proto.code", "com.google.proto.code",
                                                 ".com.google.proto.code",
                                                 "info", "proto.info", "google.proto.info", "com.google.proto.info",
                                                 ".com.google.proto.info"));
  }
}

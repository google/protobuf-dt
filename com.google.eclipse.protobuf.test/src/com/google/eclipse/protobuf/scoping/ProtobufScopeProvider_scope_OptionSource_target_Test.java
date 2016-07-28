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
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION_SOURCE__TARGET;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufScopeProvider#getScope(OptionSource, EReference)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_OptionSource_target_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // option optimize_for = SPEED;
  @Test public void should_provide_sources_for_native_option() {
    Option option = xtext.find("optimize_for", Option.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("java_package", "java_outer_classname",
        "java_multiple_files", "java_generate_equals_and_hash", "optimize_for",
        "cc_generic_services", "java_generic_services", "py_generic_services"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional Type type = 1 [ctype = STRING];
  // }
  @Test public void should_provide_sources_for_native_field_option() {
    NativeFieldOption option = xtext.find("ctype", NativeFieldOption.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("ctype", "packed", "deprecated",
        "experimental_map_key"));
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
  @Test public void should_provide_sources_for_custom_option() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "proto.code", "google.proto.code",
        "com.google.proto.code", "info", "proto.info", "google.proto.info",
        "com.google.proto.info"));
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
  @Test public void should_provide_imported_sources_for_custom_option() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("test.proto.code", "test.proto.info"));
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
  @Test public void should_provide_imported_sources_for_custom_option_with_equal_package() {
    Option option = xtext.find("code", ")", Option.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "proto.code", "google.proto.code",
        "com.google.proto.code", "info", "proto.info", "google.proto.info",
        "com.google.proto.info"));
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
  @Test public void should_provide_sources_for_custom_field_option() {
    CustomFieldOption option = xtext.find("code", ")", CustomFieldOption.class);
    IScope scope = scopeProvider.getScope(option.getSource(), OPTION_SOURCE__TARGET);
    assertThat(descriptionsIn(scope), contain("code", "proto.code", "google.proto.code",
        "com.google.proto.code", "info", "proto.info", "google.proto.info",
        "com.google.proto.info"));
  }
}

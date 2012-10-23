/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainAllFields.containAll;
import static com.google.eclipse.protobuf.junit.matchers.ContainAllNames.containAll;

import java.util.Collection;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.scoping.ProtoDescriptor;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=147">Issue 147</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue147_AddSupportForGroupOptions_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // message Person {
  //   repeated group membership = 1 [deprecated = true] {
  //     required int64 groupId = 2;
  //   }
  // }
  @Test public void should_provide_fields_for_native_option() {
    NativeFieldOption option = xtext.find("deprecated", NativeFieldOption.class);
    IScope scope = scopeProvider.scope_OptionSource_target(option.getSource(), reference);
    Group group = xtext.find("membership", Group.class);
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<MessageField> optionSources = descriptor.availableOptionsFor(group);
    assertThat(descriptionsIn(scope), containAll(optionSources));
  }

  // syntax = "proto2";
  //
  // package com.google.proto;
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FieldOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1001;
  // }
  //
  // message Person {
  //   repeated group membership = 1 [(code) = 68] {
  //     required int64 groupId = 2;
  //   }
  // }
  @Test public void should_provide_fields_for_custom_option() {
    CustomFieldOption option = xtext.find("code", ")", CustomFieldOption.class);
    IScope scope = scopeProvider.scope_OptionSource_target(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("code", "proto.code", "google.proto.code", "com.google.proto.code",
                                                 ".com.google.proto.code",
                                                 "info", "proto.info", "google.proto.info", "com.google.proto.info",
                                                 ".com.google.proto.info"));
  }
}

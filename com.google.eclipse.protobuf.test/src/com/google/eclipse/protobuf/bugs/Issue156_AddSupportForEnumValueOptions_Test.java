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
import static com.google.eclipse.protobuf.junit.matchers.ContainAllNames.containAll;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=156">Issue 156</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue156_AddSupportForEnumValueOptions_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.EnumValueOptions {
  //   optional bool active = 1000;
  // }
  //
  // enum PhoneType {
  //   HOME = 0 [(active) = true];
  // }
  @Test public void should_provide_fields_for_custom_field_option() {
    CustomFieldOption option = xtext.find("active", ")", CustomFieldOption.class);
    IScope scope = scopeProvider.scope_OptionSource_target(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("active", ".active"));
  }
}

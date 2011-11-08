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

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=156">Issue 156</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue156_AddSupportForEnumValueOptions_Test {

  private static EReference reference;

  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private ProtobufScopeProvider provider;

  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

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
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("active", ".active"));
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.scoping.OptionType.FILE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtoDescriptor#enumTypeOf(MessageField)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtoDescriptor_enumTypeOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ProtoDescriptorProvider descriptorProvider;
  private ProtoDescriptor descriptor;

  @Before public void setUp() {
    descriptor = descriptorProvider.primaryDescriptor();
  }

  @Test public void should_return_Enum_if_field_type_is_enum() {
    MessageField option = descriptor.option("optimize_for", FILE);
    Enum anEnum = descriptor.enumTypeOf(option);
    assertThat(anEnum.getName(), equalTo("OptimizeMode"));
  }

  @Test public void should_return_null_if_field_is_null() {
    assertNull(descriptor.enumTypeOf(null));
  }
}

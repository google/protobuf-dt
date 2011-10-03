/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.model.OptionType.FILE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * Tests for <code>{@link ProtoDescriptor#enumTypeOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtoDescriptor_enumTypeOf_Test {

  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();

  private ProtoDescriptor descriptor;

  @Before public void setUp() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    descriptor = descriptorProvider.primaryDescriptor();
  }

  @Test public void should_return_Enum_if_property_type_is_enum() {
    Property option = descriptor.option("optimize_for", FILE);
    Enum anEnum = descriptor.enumTypeOf(option);
    assertThat(anEnum.getName(), equalTo("OptimizeMode"));
  }

  @Test public void should_return_null_if_property_is_null() {
    assertThat(descriptor.enumTypeOf(null), nullValue());
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.integrationTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.junit.model.find.FieldOptionFinder.findCustomFieldOption;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

/**
 * Tests for <code>{@link FieldOptions#fieldFrom(CustomFieldOption)}</code>.
 * 
 * alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_fieldFrom_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Protobuf root;
  private FieldOptions fieldOptions;

  @Before public void setUp() {
    root = xtext.root();
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
  }
  
  //  import 'google/protobuf/descriptor.proto';
  //  
  //  message Custom {
  //    optional int32 count = 1;
  //  }
  //  
  //  extend google.protobuf.FieldOptions {
  //    optional Custom custom = 1000;
  //  }
  //  
  //  message Person {
  //    optional boolean active = 1 [(custom).count = 6];
  //  }
  @Test public void should_return_property_field() {
    CustomFieldOption option = findCustomFieldOption(name("custom"), in(root));
    Property p = fieldOptions.fieldFrom(option);
    assertThat(p.getName(), equalTo("count"));
  }
}

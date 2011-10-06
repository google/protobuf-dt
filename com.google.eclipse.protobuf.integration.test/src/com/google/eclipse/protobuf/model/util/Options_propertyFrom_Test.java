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
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.OptionFinder.findOption;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

/**
 * Tests for <code>{@link Options#propertyFrom(Option)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_propertyFrom_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Protobuf root;
  private Options options;

  @Before public void setUp() {
    root = xtext.root();
    options = xtext.getInstanceOf(Options.class);
  }

  // option java_package = 'com.google.eclipse.protobuf.tests';
  @Test public void should_return_property_of_native_option() {
    Option option = findOption(name("java_package"), in(root));
    Property p = options.propertyFrom(option);
    assertThat(p.getName(), equalTo("java_package"));
  }

  // import 'google/protobuf/descriptor.proto';
  //  
  // extend google.protobuf.FileOptions {
  //   optional string encoding = 1000;
  // }
  //  
  // option (encoding) = 'UTF-8';
  @Test public void should_return_property_of_custom_option() {
    Option option = findOption(name("encoding"), in(root));
    Property p = options.propertyFrom(option);
    assertThat(p.getName(), equalTo("encoding"));
  }
}

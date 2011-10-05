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
import static com.google.eclipse.protobuf.junit.model.find.ExtendMessageFinder.findExtendMessage;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.model.OptionType.FILE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.OptionType;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

/**
 * Tests for <code>{@link Options#isExtendingOptionMessage(EObject, OptionType)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_isExtendingOptionMessage_Test {
  
  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Options options;

  @Before public void setUp() {
    options = xtext.getInstanceOf(Options.class);
  }

  @Test public void should_return_true_if_name_of_extended_message_is_equal_to_message_name_in_OptionType() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("extend google.protobuf.FileOptions {      ")
         .append("  optional string encoding = 1000;        ")
         .append("}                                         ");
    Protobuf root = xtext.parseText(proto);
    ExtendMessage extend = findExtendMessage(name("FileOptions"), in(root));
    boolean result = options.isExtendingOptionMessage(extend, FILE);
    assertThat(result, equalTo(true));
  }
  
  @Test public void should_return_false_if_given_EObject_is_not_ExtendMessage() {
    Import anImport = mock(Import.class);
    boolean result = options.isExtendingOptionMessage(anImport, FILE);
    assertThat(result, equalTo(false));
  }

  @Test public void should_return_false_if_name_of_extended_message_is_not_equal_to_message_name_in_OptionType() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("extend google.protobuf.FieldOptions {     ")
         .append("  optional string encoding = 1000;        ")
         .append("}                                         ");
    Protobuf root = xtext.parseText(proto);
    ExtendMessage extend = findExtendMessage(name("FieldOptions"), in(root));
    boolean result = options.isExtendingOptionMessage(extend, FILE);
    assertThat(result, equalTo(false));
  }
}

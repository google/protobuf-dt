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
import static com.google.eclipse.protobuf.junit.model.find.FieldOptionFinder.findCustomFieldOption;
import static com.google.eclipse.protobuf.junit.model.find.MessageFinder.findMessage;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.OptionFinder.findCustomOption;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.scoping.ContainAllPropertiesInMessage.containAllPropertiesIn;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_SimplePropertyRef_property(SimplePropertyRef, EReference)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_SimplePropertyRef_property_Test {

  private static EReference reference;
  
  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }
  
  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Protobuf root;
  private ProtobufScopeProvider provider;
  
  @Before public void setUp() {
    root = xtext.root();
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // import 'google/protobuf/descriptor.proto';
  //  
  // message Type {
  //   optional int32 code = 1;
  //   optional string name = 2;
  // }
  //  
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;  
  // }
  //  
  // option (type).code = 68;
  @Test public void should_provide_Property_fields_for_custom_option_field() {
    CustomOption option = findCustomOption(name("type"), in(root));
    IScope scope = provider.scope_SimplePropertyRef_property(option.getPropertyField(), reference);
    Message typeMessage = findMessage(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllPropertiesIn(typeMessage));
  }

  // import 'google/protobuf/descriptor.proto';
  //  
  // message Type {
  //   optional int32 code = 1;
  //   optional string name = 2;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //  
  // message Person {
  //   optional boolean active = 1 [(type).code = 68];
  // }
  @Test public void should_provide_Property_fields_for_custom_field_option_field() {
    CustomFieldOption option = findCustomFieldOption(name("type"), in(root));
    IScope scope = provider.scope_SimplePropertyRef_property(option.getPropertyField(), reference);
    Message typeMessage = findMessage(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllPropertiesIn(typeMessage));
  }
}

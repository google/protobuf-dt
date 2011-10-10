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
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_TypeRef_type(TypeRef, EReference)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_TypeRef_type_Test {

  private static EReference reference;
  
  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }
  
  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private ProtobufScopeProvider provider;
  
  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // package com.google.proto;
  // 
  // enum Type {
  //   PERSONAL = 0;
  //   BUSINESS = 1;
  // }
  //
  // message Address {
  //   optional int32 number = 1;
  //   optional string street = 2;
  //   optional string city = 3;
  //   optional int32 zipCode = 4;
  // }
  //  
  // message Contact {
  //   optional Type type = 1;  
  // }
  @Test public void should_provide_Property_fields_for_custom_option_field() {
    Property p = xtext.find("type", Property.class);
    IScope scope = provider.scope_TypeRef_type(typeOf(p), reference);
    assertThat(descriptionsIn(scope), containAll("Type", "proto.Type", "google.proto.Type", "com.google.proto.Type", 
                                                 ".com.google.proto.Type",
                                                 "Address", "proto.Address", "google.proto.Address", 
                                                 "com.google.proto.Address", ".com.google.proto.Address",
                                                 "Contact", "proto.Contact", "google.proto.Contact",
                                                 "com.google.proto.Contact", ".com.google.proto.Contact"));
  }
  
  private static TypeRef typeOf(Property p) {
    return (TypeRef) p.getType();
  }
}

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
import static com.google.eclipse.protobuf.scoping.ContainAllProperties.containAll;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.scoping.OptionType.FIELD;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=147">Issue 147</a>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue147_AddSupportForGroupOptions_Test {

  private static EReference reference;
  
  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }
  
  @Rule public XtextRule xtext = createWith(integrationTestSetup());
  
  private ProtobufScopeProvider provider;
  
  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // message Person {
  //   repeated group membership = 1 [deprecated = true] {
  //     required int64 groupId = 2;
  //   }
  // }
  @Test public void should_provide_Property_fields_for_native_option() {
    NativeFieldOption option = xtext.find("deprecated", NativeFieldOption.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    Collection<Property> fieldOptions = descriptor().optionsOfType(FIELD);
    assertThat(descriptionsIn(scope), containAll(fieldOptions));
  }
  
  private ProtoDescriptor descriptor() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    return descriptorProvider.primaryDescriptor();
  }
  
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
  @Test public void should_provide_Property_fields_for_custom_option() {
    CustomFieldOption option = xtext.find("code", ")", CustomFieldOption.class);
    IScope scope = provider.scope_OptionSource_optionField(option.getSource(), reference);
    assertThat(descriptionsIn(scope), containAll("code", "proto.code", "google.proto.code", "com.google.proto.code", 
                                                 ".com.google.proto.code",
                                                 "info", "proto.info", "google.proto.info", "com.google.proto.info", 
                                                 ".com.google.proto.info"));
  }
}

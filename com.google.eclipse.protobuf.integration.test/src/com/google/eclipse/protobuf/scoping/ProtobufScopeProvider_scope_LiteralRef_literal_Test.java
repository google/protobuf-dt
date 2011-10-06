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
import static com.google.eclipse.protobuf.junit.model.find.EnumFinder.findEnum;
import static com.google.eclipse.protobuf.junit.model.find.FieldOptionFinder.findFieldOption;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.OptionFinder.findOption;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.scoping.ContainAllLiteralsInEnum.containAllLiteralsIn;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.LiteralRef;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_LiteralRef_literal(LiteralRef, EReference)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_LiteralRef_literal_Test {

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
  
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  // 
  // message Person {
  //   optional Type type = 1 [default = ONE];
  // }
  @Test public void should_provide_Literals_for_default_value() {
    FieldOption option = findFieldOption(name("default"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }
  
  // option optimize_for = SPEED;
  @Test public void should_provide_Literals_for_native_option() {
    Option option = findOption(name("optimize_for"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum optimizeModeEnum = descriptor().enumByName("OptimizeMode");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(optimizeModeEnum));
  }
  
  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //  
  // option (type) = ONE; 
  @Test public void should_provide_Literals_for_custom_option() {
    Option option = findOption(name("type"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // message Info {
  //   optional Type type = 1;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Info info = 1000;
  // }
  //  
  // option (info).type = ONE; 
  @Test public void should_provide_Literals_for_property_of_custom_option() {
    Option option = findOption(name("info"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  private static LiteralRef valueOf(Option option) {
    return (LiteralRef) option.getValue();
  }
  
  //  message Person {
  //    optional Type type = 1 [ctype = STRING];
  //  }
  @Test public void should_provide_Literals_for_native_field_option() {
    FieldOption option = findFieldOption(name("ctype"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum cTypeEnum = descriptor().enumByName("CType");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(cTypeEnum));
  }
  
  private ProtoDescriptor descriptor() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    return descriptorProvider.primaryDescriptor();
  }

  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //  
  // message Person {
  //   optional boolean active = 1 [(type) = ONE];
  // }
  @Test public void should_provide_Literals_for_custom_field_option() {
    FieldOption option = findFieldOption(name("type"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  // import 'google/protobuf/descriptor.proto';
  //
  // enum Type {
  //   ONE = 0;
  //   TWO = 1;
  // }
  //
  // message Info {
  //   optional Type type = 1;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Info info = 1000;
  // }
  //  
  // message Person {
  //   optional boolean active = 1 [(info).type = ONE];
  // }
  @Test public void should_provide_Literals_for_property_of_custom_field_option() {
    FieldOption option = findFieldOption(name("info"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  private static LiteralRef valueOf(FieldOption option) {
    return (LiteralRef) option.getValue();
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

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
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
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
  
  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();
  
  private ProtobufScopeProvider provider;
  
  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }
  
  @Test public void should_provide_Literals_for_default_value() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("enum Type {                              ")
         .append("  ONE = 0;                               ")
         .append("  TWO = 1;                               ")
         .append("}                                        ")
         .append("                                         ")
         .append("message Person {                         ")
         .append("  optional Type type = 1 [default = ONE];")
         .append("}                                        ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("default"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }
  
  @Test public void should_provide_Literals_for_native_option() {
    Protobuf root = xtext.parseText("option optimize_for = SPEED;");
    Option option = findOption(name("optimize_for"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum optimizeModeEnum = descriptor().enumByName("OptimizeMode");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(optimizeModeEnum));
  }
  
  @Test public void should_provide_Literals_for_custom_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("enum Type {                               ")
         .append("  ONE = 0;                                ")
         .append("  TWO = 1;                                ")
         .append("}                                         ")
         .append("                                          ")
         .append("extend google.protobuf.FileOptions {      ")
         .append("  optional Type type = 1000;              ")
         .append("}                                         ")
         .append("                                          ")
         .append("option (type) = ONE;                      ");
    Protobuf root = xtext.parseText(proto);
    Option option = findOption(name("type"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  @Test public void should_provide_Literals_for_property_of_custom_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("enum Type {                               ")
         .append("  ONE = 0;                                ")
         .append("  TWO = 1;                                ")
         .append("}                                         ")
         .append("                                          ")
         .append("message Info {                            ")
         .append("  optional Type type = 1;                 ")
         .append("}                                         ")
         .append("                                          ")
         .append("extend google.protobuf.FileOptions {      ")
         .append("  optional Info info = 1000;              ")
         .append("}                                         ")
         .append("                                          ")
         .append("option (info).type = ONE;                 ");
    Protobuf root = xtext.parseText(proto);
    Option option = findOption(name("info"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  private static LiteralRef valueOf(Option option) {
    return (LiteralRef) option.getValue();
  }
  
  @Test public void should_provide_Literals_for_native_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                          ")
         .append("  optional Type type = 1 [ctype = STRING];")
         .append("}                                         ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("ctype"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum cTypeEnum = descriptor().enumByName("CType");
    assertThat(descriptionsIn(scope), containAllLiteralsIn(cTypeEnum));
  }
  
  private ProtoDescriptor descriptor() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    return descriptorProvider.primaryDescriptor();
  }

  @Test public void should_provide_Literals_for_custom_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';   ")
         .append("                                             ")
         .append("enum Type {                                  ")
         .append("  ONE = 0;                                   ")
         .append("  TWO = 1;                                   ")
         .append("}                                            ")
         .append("                                             ")
         .append("extend google.protobuf.FieldOptions {        ")
         .append("  optional Type type = 1000;                 ")
         .append("}                                            ")
         .append("                                             ")
         .append("message Person {                             ")
         .append("  optional boolean active = 1 [(type) = ONE];")
         .append("}                                            ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("type"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  @Test public void should_provide_Literals_for_property_of_custom_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';        ")
         .append("                                                  ")
         .append("enum Type {                                       ")
         .append("  ONE = 0;                                        ")
         .append("  TWO = 1;                                        ")
         .append("}                                                 ")
         .append("                                                  ")
         .append("message Info {                                    ")
         .append("  optional Type type = 1;                         ")
         .append("}                                                 ")
         .append("                                                  ")
         .append("extend google.protobuf.FieldOptions {             ")
         .append("  optional Info info = 1000;                      ")
         .append("}                                                 ")
         .append("                                                  ")
         .append("message Person {                                  ")
         .append("  optional boolean active = 1 [(info).type = ONE];")
         .append("}                                                 ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("info"), in(root));
    IScope scope = provider.scope_LiteralRef_literal(valueOf(option), reference);
    Enum typeEnum = findEnum(name("Type"), in(root));
    assertThat(descriptionsIn(scope), containAllLiteralsIn(typeEnum));
  }

  private static LiteralRef valueOf(FieldOption option) {
    return (LiteralRef) option.getValue();
  }
}

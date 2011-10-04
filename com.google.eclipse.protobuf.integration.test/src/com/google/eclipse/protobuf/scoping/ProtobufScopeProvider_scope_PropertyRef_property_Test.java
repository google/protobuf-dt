/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.model.find.ExtendMessageFinder.findExtendMessage;
import static com.google.eclipse.protobuf.junit.model.find.FieldOptionFinder.*;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.OptionFinder.findOption;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.model.OptionType.*;
import static com.google.eclipse.protobuf.scoping.ContainAllProperties.containAll;
import static com.google.eclipse.protobuf.scoping.ContainNames.containAll;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.*;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import java.util.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_PropertyRef_property(PropertyRef, EReference)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_PropertyRef_property_Test {

  private static EReference reference;
  
  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }
  
  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();
  
  private ProtobufScopeProvider provider;
  
  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  @Test public void should_provide_Property_fields_for_native_option() {
    Protobuf root = xtext.parseText("option optimize_for = SPEED;");
    Option option = findOption(name("optimize_for"), in(root));
    IScope scope = provider.scope_PropertyRef_property(option.getProperty(), reference);
    Collection<Property> fileOptions = descriptor().optionsOfType(FILE);
    assertThat(descriptionsIn(scope), containAll(fileOptions));
  }
  
  @Test public void should_provide_Property_fields_for_native_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                          ")
         .append("  optional Type type = 1 [ctype = STRING];")
         .append("}                                         ");
    Protobuf root = xtext.parseText(proto);
    NativeFieldOption option = findNativeFieldOption(name("ctype"), in(root));
    IScope scope = provider.scope_PropertyRef_property(option.getProperty(), reference);
    Collection<Property> fieldOptions = descriptor().optionsOfType(FIELD);
    assertThat(descriptionsIn(scope), containAll(fieldOptions));
  }

  private ProtoDescriptor descriptor() {
    ProtoDescriptorProvider descriptorProvider = xtext.getInstanceOf(ProtoDescriptorProvider.class);
    return descriptorProvider.primaryDescriptor();
  }

  @Test public void should_provide_Property_fields_for_custom_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package com.google.proto;                 ")
         .append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("extend google.protobuf.FileOptions {      ")
         .append("  optional int32 code = 1000;             ")
         .append("  optional string name = 1001;            ")
         .append("}                                         ")
         .append("                                          ")
         .append("option (code) = 68;                       ");
    Protobuf root = xtext.parseText(proto);
    Option option = findOption(name("code"), in(root));
    IScope scope = provider.scope_PropertyRef_property(option.getProperty(), reference);
    ExtendMessage extendFileOptionsMessage = findExtendMessage(name("FileOptions"), in(root));
    Map<String, Property> properties = propertiesOf(extendFileOptionsMessage);
    Property code = properties.get("code");
    assertThat(descriptions(scope.getElements(code)), containAll("code", "proto.code", "google.proto.code", 
                                                                 "com.google.proto.code", ".com.google.proto.code"));
    Property name = properties.get("name");
    assertThat(descriptions(scope.getElements(name)), containAll("name", "proto.name", "google.proto.name", 
                                                                 "com.google.proto.name", ".com.google.proto.name"));
  }

  @Test public void should_provide_Property_fields_for_custom_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package com.google.proto;                   ")
         .append("import 'google/protobuf/descriptor.proto';  ")
         .append("                                            ")
         .append("extend google.protobuf.FieldOptions {       ")
         .append("  optional int32 code = 1000;               ")
         .append("  optional string name = 1001;              ")
         .append("}                                           ")
         .append("                                            ")
         .append("message Person {                            ")
         .append("  optional boolean active = 1 [(code) = 68];")
         .append("}                                           ");
    Protobuf root = xtext.parseText(proto);
    CustomFieldOption option = findCustomFieldOption(name("code"), in(root));
    IScope scope = provider.scope_PropertyRef_property(option.getProperty(), reference);
    ExtendMessage extendFileOptionsMessage = findExtendMessage(name("FieldOptions"), in(root));
    Map<String, Property> properties = propertiesOf(extendFileOptionsMessage);
    Property code = properties.get("code");
    assertThat(descriptions(scope.getElements(code)), containAll("code", "proto.code", "google.proto.code", 
                                                                 "com.google.proto.code", ".com.google.proto.code"));
    Property name = properties.get("name");
    assertThat(descriptions(scope.getElements(name)), containAll("name", "proto.name", "google.proto.name", 
                                                                 "com.google.proto.name", ".com.google.proto.name"));
  }

  private static Map<String, Property> propertiesOf(ExtendMessage m) {
    Map<String, Property> properties = new HashMap<String, Property>();
    for (Property p : getAllContentsOfType(m, Property.class)) {
      properties.put(p.getName(), p);
    }
    return properties;
  }
}

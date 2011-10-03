/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.model.find.FieldOptionFinder.findFieldOption;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_LiteralRef_literal(LiteralRef, EReference)}</code>
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
    LiteralRef ref = (LiteralRef) option.getValue();
    IScope scope = provider.scope_LiteralRef_literal(ref, reference);
    IEObjectDescriptions descriptions = descriptionsIn(scope);
    Literal one = (Literal) descriptions.objectDescribedAs("ONE");
    assertThat(one.getName(), equalTo("ONE"));
    Literal two = (Literal) descriptions.objectDescribedAs("TWO");
    assertThat(two.getName(), equalTo("TWO"));
  }
}

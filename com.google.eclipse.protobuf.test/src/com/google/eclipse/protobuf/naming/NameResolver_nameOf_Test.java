/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link NameResolver#nameOf(EObject)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NameResolver_nameOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  private NameResolver resolver;
  
  @Before public void setUp() {
    resolver = xtext.getInstanceOf(NameResolver.class);
  }
  
  // syntax = "proto2";
  //
  // message Person {}
  @Test public void should_return_name_of_Message() {
    Message message = xtext.find("Person", Message.class);
    String name = resolver.nameOf(message);
    assertThat(name, equalTo("Person"));
  }
  
  // syntax = "proto2";
  //
  // package com.google.proto.test;
  @Test public void should_return_name_of_Package() {
    Package aPackage = xtext.find("com.google.proto.test", Package.class);
    String name = resolver.nameOf(aPackage);
    assertThat(name, equalTo("com.google.proto.test"));    
  }
}

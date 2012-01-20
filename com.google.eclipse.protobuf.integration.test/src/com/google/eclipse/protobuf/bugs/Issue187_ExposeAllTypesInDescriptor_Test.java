/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainNames.contain;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=187">Issue 187</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue187_ExposeAllTypesInDescriptor_Test {
  private static EReference reference;

  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  //
  // message FieldType {
  //   optional google.protobuf.FieldDescriptorProto.Type type = 1;
  // }
  @Test public void should_see_types_from_descriptor_other_than_Messages() {
    MessageField field = xtext.find("type", MessageField.class);
    TypeLink type = field.getType();
    IScope scope = scopeProvider.scope_ComplexTypeLink_target((ComplexTypeLink) type, reference);
    assertThat(descriptionsIn(scope), contain("google.protobuf.FieldDescriptorProto.Type"));
  }
}

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
import static com.google.eclipse.protobuf.scoping.ContainNames.contain;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=157">Issue 157</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue157_GroupsShouldBeTypes_Test {

  private static EReference reference;

  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private ProtobufScopeProvider provider;

  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // syntax = "proto2";
  //
  // message Root {
  //   optional group MyGroup = 1 {}
  //
  //   message NestedMessage {
  //     optional MyGroup mygroup = 2207766;
  //   }
  // }
  @Test public void should_treat_groups_as_types() {
    MessageField field = xtext.find("mygroup", MessageField.class);
    IScope scope = provider.scope_ComplexTypeLink_target((ComplexTypeLink) field.getType(), reference);
    assertThat(descriptionsIn(scope), contain("Root.MyGroup", "MyGroup"));
  }
}

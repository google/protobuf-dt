/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.matchers.ContainNames.contain;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=157">Issue 157</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue157_GroupsShouldBeTypes_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtobufScopeProvider scopeProvider;

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
    IScope scope = scopeProvider.scope_ComplexTypeLink_target((ComplexTypeLink) field.getType(), reference);
    assertThat(descriptionsIn(scope), contain("Root.MyGroup", "MyGroup"));
  }
}

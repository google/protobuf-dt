/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertSame;

import static com.google.eclipse.protobuf.junit.IEObjectDescriptions.descriptionsIn;
import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.scoping.ProtobufScopeProvider;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=189">Issue 189</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue189_ScopingShouldLookForClosestType {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtobufScopeProvider scopeProvider;

  // syntax = "proto2";
  //
  // message Test {
  //   enum Status {
  //     ACTIVE = 0;
  //     INACTIVE = 1;
  //   }
  //   optional Status status = 1 [default = ACTIVE];
  // }
  //
  // enum Status {
  //   DRAFT = 0;
  //   READY = 1;
  // }
  @Test public void should_find_closest_type_possible() {
    Literal active = xtext.find("ACTIVE", " = 0", Literal.class);
    MessageField field = xtext.find("status", MessageField.class);
    ComplexTypeLink link = (ComplexTypeLink) field.getType();
    IScope scope = scopeProvider.scope_ComplexTypeLink_target(link, reference);
    EObject status = descriptionsIn(scope).objectDescribedAs("Status");
    assertSame(active.eContainer(), status);
  }
}

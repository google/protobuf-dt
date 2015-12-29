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
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=167">Issue 167</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue167_PackageScopingWithNestedTypes_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private EReference reference;
  @Inject private ProtobufScopeProvider scopeProvider;

  // // Create file types.proto
  //
  // syntax = 'proto2';
  // package com.google.proto.base.shared;
  //
  // message Outer {
  //   enum Type {
  //     ONE = 1;
  //     TWO = 2;
  //   }
  // }

  // syntax = "proto2";
  // package com.google.proto.project.shared;
  //
  // import "types.proto";
  //
  // message Summary {
  //   repeated base.shared.Outer.Type type = 1;
  // }
  @Test public void should_include_package_intersection() {
    MessageField field = xtext.find("type", " =", MessageField.class);
    IScope scope = scopeProvider.scope_ComplexTypeLink_target((ComplexTypeLink) field.getType(), reference);
    assertThat(descriptionsIn(scope), contain("base.shared.Outer.Type", "proto.base.shared.Outer.Type",
                                              "google.proto.base.shared.Outer.Type",
                                              "com.google.proto.base.shared.Outer.Type"));
  }
}

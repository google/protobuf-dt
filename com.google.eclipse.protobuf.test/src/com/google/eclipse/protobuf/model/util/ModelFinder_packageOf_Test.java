/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

/**
 * Tests for <code>{@link ModelFinder#packageOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_packageOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // package person.test;
  //  
  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_package_if_proto_has_one() {
    Property id = xtext.find("id", Property.class);
    Package aPackage = finder.packageOf(id);
    assertThat(aPackage.getName(), equalTo("person.test"));
  }

  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_null_if_proto_does_not_have_package() {
    Property id = xtext.find("id", Property.class);
    Package aPackage = finder.packageOf(id);
    assertThat(aPackage, nullValue());
  }
}

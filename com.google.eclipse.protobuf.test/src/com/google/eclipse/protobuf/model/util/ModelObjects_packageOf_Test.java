/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.ecore.EObject;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ModelObjects#packageOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjects_packageOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ModelObjects modelObjects;

  // syntax = "proto2";
  //
  // package person.test;
  //
  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_package_if_proto_has_one() {
    MessageField field = xtext.find("id", MessageField.class);
    Package aPackage = modelObjects.packageOf(field);
    assertThat(aPackage.getName(), equalTo("person.test"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_null_if_proto_does_not_have_package() {
    MessageField field = xtext.find("id", MessageField.class);
    assertNull(modelObjects.packageOf(field));
  }
}

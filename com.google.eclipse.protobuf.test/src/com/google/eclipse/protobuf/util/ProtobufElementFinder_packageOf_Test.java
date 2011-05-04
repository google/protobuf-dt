/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.junit.util.Finder.findProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link ProtobufElementFinder#packageOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementFinder_packageOf_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ProtobufElementFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ProtobufElementFinder.class);
  }

  @Test public void should_return_package_if_proto_has_one() {
    StringBuilder proto = new StringBuilder();
    proto.append("package person.test;    ")
         .append("                        ")
         .append("message Person {        ")
         .append("  optional int32 id = 1;")
         .append("}                       ");
    Protobuf root = xtext.parse(proto);
    Property id = findProperty("id", root);
    Package aPackage = finder.packageOf(id);
    assertThat(aPackage.getName(), equalTo("person.test"));
  }

  @Test public void should_return_null_if_proto_does_not_have_package() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {        ")
         .append("  optional int32 id = 1;")
         .append("}                       ");
    Protobuf root = xtext.parse(proto);
    Property id = findProperty("id", root);
    Package aPackage = finder.packageOf(id);
    assertThat(aPackage, nullValue());
  }
}

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
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link ProtobufElementFinder#rootOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementFinder_rootOf_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ProtobufElementFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ProtobufElementFinder.class);
  }

  @Test public void should_return_root_of_proto() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property name = findProperty("name", root);
    assertThat(finder.rootOf(name), sameInstance(root));
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.find.Name.name;
import static com.google.eclipse.protobuf.junit.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.find.Root.in;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.util.ModelFinder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ModelFinder#rootOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_rootOf_Test {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  @Test public void should_return_root_of_proto() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property name = findProperty(name("name"), in(root));
    assertThat(finder.rootOf(name), sameInstance(root));
  }
}

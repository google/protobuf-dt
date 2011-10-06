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
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

/**
 * Tests for <code>{@link ModelFinder#rootOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_rootOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Protobuf root;
  private ModelFinder finder;

  @Before public void setUp() {
    root = xtext.root();
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_root_of_proto() {
    Property name = findProperty(name("name"), in(root));
    assertThat(finder.rootOf(name), sameInstance(root));
  }
}

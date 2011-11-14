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
import static org.junit.Assert.assertSame;

import java.util.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ModelFinder#localExtensionsOf(Message)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_localExtensionsFrom_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  //
  // extend Person {}
  @Test public void should_return_extensions_of_message() {
    Message m = xtext.find("Person", " {", Message.class);
    List<Extend> extensions = new ArrayList<Extend>(finder.localExtensionsOf(m));
    Message referred = extensions.get(0).getMessage().getType();
    assertSame(m, referred);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.parser;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import org.antlr.runtime.CharStream;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Proto2OnlyParser#doParse(String, CharStream, NodeModelBuilder, int)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Proto2OnlyParser_doParse_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  // // ignore errors
  // c++header #include "test/common/proto_class.h"
  @Test public void should_recognize_proto1_syntax() {
    Protobuf root = xtext.root();
    assertThat(root, instanceOf(NonProto2Protobuf.class));
  }
}

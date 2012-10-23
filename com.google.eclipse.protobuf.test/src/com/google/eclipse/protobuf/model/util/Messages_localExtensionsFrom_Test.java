/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.junit.Assert.assertSame;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Messages#localExtensionsOf(Message)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages_localExtensionsFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Messages messages;

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  //
  // extend Person {}
  @Test public void should_return_extensions_of_message() {
    Message m = xtext.find("Person", " {", Message.class);
    List<TypeExtension> extensions = newArrayList(messages.localExtensionsOf(m));
    Message referred = (Message) extensions.get(0).getType().getTarget();
    assertSame(m, referred);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufDiagnostic#ProtobufDiagnostic(String, String[], String, INode)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDiagnostic_constructor_Test {
  private static INode node;

  @BeforeClass public static void setUpOnce() {
    node = mock(INode.class);
  }

  @Test(expected = NullPointerException.class)
  public void should_throw_exception_if_data_is_null() {
    new ProtobufDiagnostic("1000", null, "message", node);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_throw_exception_if_data_contains_nulls() {
    new ProtobufDiagnostic("1000", new String[] { null }, "message", node);
  }

  @Test(expected = NullPointerException.class)
  public void should_throw_exception_if_node_is_null() {
    new ProtobufDiagnostic("1000", new String[0], "message", null);
  }

  @SuppressWarnings("unchecked")
  @Test public void should_create_new_instance() {
    String[] data = { "abc.proto" };
    ProtobufDiagnostic d = new ProtobufDiagnostic("1000", data, "message", node);
    assertNotNull(d);
    assertThat(d.getCode(), equalTo("1000"));
    assertThat(d.getData(), allOf(equalTo(data), not(sameInstance(data))));
    assertThat(d.getMessage(), equalTo("message"));
    assertSame(node, d.getNode());
  }
}

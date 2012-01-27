/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static com.google.eclipse.protobuf.linking.ProtobufDiagnosticMatcher.wasCreatedFrom;
import static org.eclipse.xtext.diagnostics.Severity.WARNING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.*;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufResource#createDiagnostic(Triple, DiagnosticMessage)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResource_createDiagnostic_Test {
  private static Triple<EObject, EReference, INode> triple;

  @BeforeClass public static void setUpOnce() {
    triple = Tuples.create(mock(EObject.class), mock(EReference.class), mock(INode.class));
  }

  private DiagnosticMessage message;
  private ProtobufResource resource;

  @Before public void setUp() {
    message = new DiagnosticMessage("message", WARNING, "1000", new String[] { "abc.proto" });
    resource = new ProtobufResource();
  }

  @Test public void should_create_dianostic() {
    ProtobufDiagnostic d = resource.createDiagnostic(triple, message);
    assertThat(d, wasCreatedFrom(message));
    assertSame(triple.getThird(), d.getNode());
  }
}

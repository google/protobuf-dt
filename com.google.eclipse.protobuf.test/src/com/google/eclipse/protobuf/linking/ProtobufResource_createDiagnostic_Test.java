/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static org.eclipse.xtext.diagnostics.Severity.WARNING;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
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
    Diagnostic diagnostic = resource.createDiagnostic(triple, message);
    assertThat(diagnostic, instanceOf(ProtobufDiagnostic.class));
    ProtobufDiagnostic d = (ProtobufDiagnostic) diagnostic;
    assertThat(d.getCode(), equalTo(message.getIssueCode()));
    assertThat(d.getData(), equalTo(message.getIssueData()));
    assertThat(d.getMessage(), equalTo(message.getMessage()));
    assertSame(triple.getThird(), d.getNode());
  }
}

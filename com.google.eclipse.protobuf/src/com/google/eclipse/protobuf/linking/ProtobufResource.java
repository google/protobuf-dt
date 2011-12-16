/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Triple;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResource extends LazyLinkingResource {

  @Override protected Diagnostic createDiagnostic(Triple<EObject, EReference, INode> t, DiagnosticMessage message) {
    return new ProtobufDiagnostic(message.getIssueCode(), message.getIssueData(), message.getMessage(), t.getThird());
  }
}

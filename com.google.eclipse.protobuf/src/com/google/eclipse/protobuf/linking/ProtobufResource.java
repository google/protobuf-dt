/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Triple;

import com.google.eclipse.protobuf.resource.IResourceVerifier;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResource extends LazyLinkingResource {
  @Inject private IResourceVerifier resourceVerifier;

  @Override
  protected ProtobufDiagnostic createDiagnostic(Triple<EObject, EReference, INode> t, DiagnosticMessage message) {
    return new ProtobufDiagnostic(message.getIssueCode(), message.getIssueData(), message.getMessage(), t.getThird());
  }

  @Override protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
    if (resourceVerifier.shouldIgnore(uri)) {
      return;
    }
    super.doLoad(inputStream, options);
  }
}

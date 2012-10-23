/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.resolveAll;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;

import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufInTestsParser {
  private final Injector injector;

  public ProtobufInTestsParser(Injector injector) {
    this.injector = injector;
  }

  public IParseResult parseText(String text) {
    boolean ignoreSyntaxErrors = shouldIgnoreSyntaxErrorsIn(text);
    XtextResource resource = createResourceFrom(new StringInputStream(text));
    IParseResult parseResult = resource.getParseResult();
    if (ignoreSyntaxErrors || !parseResult.hasSyntaxErrors()) {
      return parseResult;
    }
    StringBuilder builder = new StringBuilder();
    builder.append("Syntax errors:");
    for (INode error : parseResult.getSyntaxErrors()) {
      builder.append(lineSeparator()).append("- ").append(error.getSyntaxErrorMessage());
    }
    throw new IllegalStateException(builder.toString());
  }

  private boolean shouldIgnoreSyntaxErrorsIn(String text) {
    return text.startsWith("// ignore errors");
  }

  private XtextResource createResourceFrom(InputStream input) {
    return createResourceFrom(input, createURI("file:/usr/local/project/src/protos/mytestmodel.proto"));
  }

  private XtextResource createResourceFrom(InputStream input, URI uri) {
    XtextResourceSet resourceSet = getInstanceOf(XtextResourceSet.class);
    resourceSet.setClasspathURIContext(getClass());
    XtextResource resource = (XtextResource) getInstanceOf(IResourceFactory.class).createResource(uri);
    resourceSet.getResources().add(resource);
    try {
      resource.load(input, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (resource instanceof LazyLinkingResource) {
      ((LazyLinkingResource) resource).resolveLazyCrossReferences(NullImpl);
      return resource;
    }
    resolveAll(resource);
    return resource;
  }

  private <T> T getInstanceOf(Class<T> type) {
    return injector.getInstance(type);
  }
}

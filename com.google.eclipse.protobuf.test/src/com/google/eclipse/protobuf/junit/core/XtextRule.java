/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;
import static java.util.Arrays.asList;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.resolveAll;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.*;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.*;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.util.StringInputStream;
import org.junit.rules.MethodRule;
import org.junit.runners.model.*;

import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.*;

/**
 * JUnit <code>{@link MethodRule}</code> that:
 * <ol>
 * <li>Performs configuration of a standalone Xtext environment</li>
 * <li>Creates an <code>{@link XtextResource}</code> from method-level comments</li>
 * <li>Creates .proto files in the file system based on method-level comments (if the comment starts with
 * "// Create file" followed by the name of the file to create)</li>
 * <li>Finds model objects and nodes in the created <code>{@link XtextResource}</code> (from #2)</li>
 * </ol>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class XtextRule implements MethodRule {
  private final Injector injector;
  private final CommentProcessor processor;
  private final CommentReader reader;

  private Protobuf root;
  private XtextResource resource;
  private Finder finder;

  public static XtextRule overrideRuntimeModuleWith(Module...testModules) {
    return createWith(new OverrideRuntimeModuleSetup(testModules));
  }

  public static XtextRule createWith(ISetup setup) {
    return createWith(setup.createInjectorAndDoEMFRegistration());
  }

  public static XtextRule createWith(Injector injector) {
    return new XtextRule(injector);
  }

  private XtextRule(Injector injector) {
    this.injector = injector;
    processor = new CommentProcessor();
    reader = new CommentReader();
  }

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    injector.injectMembers(target);
    root = null;
    String comments = commentsIn(method);
    if (!isEmpty(comments)) {
      parseText(comments);
      finder = new Finder(resource.getParseResult().getRootNode(), comments);
    }
    return base;
  }

  private String commentsIn(FrameworkMethod method) {
    for (String comment : reader.commentsIn(method)) {
      Object processed = processor.processComment(comment);
      if (processed instanceof String) {
        return (String) processed;
      }
    }
    return null;
  }

  public void parseText(String text) {
    boolean ignoreSyntaxErrors = shouldIgnoreSyntaxErrorsIn(text);
    resource = createResourceFrom(new StringInputStream(text));
    IParseResult parseResult = resource.getParseResult();
    root = (Protobuf) parseResult.getRootASTElement();
    if (ignoreSyntaxErrors) {
      return;
    }
    if (!parseResult.hasSyntaxErrors()) {
      if (root.getSyntax() == null) {
        throw new IllegalStateException("Please specify 'proto2' syntax");
      }
      return;
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

  public Injector injector() {
    return injector;
  }

  public XtextResource resource() {
    return resource;
  }

  public Protobuf root() {
    return root;
  }

  public <T extends EObject> T find(String name, String extra, Class<T> type, SearchOption...options) {
    return find(name + extra, name.length(), type, options);
  }

  public <T extends EObject> T find(String name, Class<T> type, SearchOption...options) {
    return find(name, name.length(), type, options);
  }

  public <T extends EObject> T find(String text, int count, Class<T> type, SearchOption...options) {
    return finder.find(text, count, type, asList(options));
  }

  public ILeafNode findNode(String text) {
    return finder.find(text);
  }

  public <T extends EObject> T findFirst(Class<T> type) {
    List<T> contents = EcoreUtil2.getAllContentsOfType(root, type);
    return (contents.isEmpty()) ? null : contents.get(0);
  }
}

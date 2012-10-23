/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import java.util.List;

import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.parser.IParseResult;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.eclipse.protobuf.junit.core.CommentReader;
import com.google.eclipse.protobuf.junit.core.OverrideRuntimeModuleSetup;
import com.google.eclipse.protobuf.junit.core.ProtobufInTestsParser;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CommentReaderRule implements MethodRule {
  private final Injector injector;

  private final CommentReader commentReader;
  private final ProtobufInTestsParser protobufParser;

  private ICompositeNode rootNode;
  private String expectedText;

  static CommentReaderRule overrideRuntimeModuleWith(Module...testModules) {
    ISetup setup = new OverrideRuntimeModuleSetup(testModules);
    Injector injector = setup.createInjectorAndDoEMFRegistration();
    return new CommentReaderRule(injector);
  }

  private CommentReaderRule(Injector injector) {
    this.injector = injector;
    commentReader = new CommentReader();
    protobufParser = new ProtobufInTestsParser(injector);
  }

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    injector.injectMembers(target);
    rootNode = null;
    List<String> comments = commentReader.commentsIn(method);
    if (comments.size() == 2) {
      parseText(comments.get(0));
      expectedText = comments.get(1);
    }
    return base;
  }

  private void parseText(String text) {
    IParseResult parseResult = protobufParser.parseText(text);
    rootNode = parseResult.getRootNode();
  }

  ICompositeNode rootNode() {
    return rootNode;
  }

  String expectedText() {
    return expectedText;
  }
}

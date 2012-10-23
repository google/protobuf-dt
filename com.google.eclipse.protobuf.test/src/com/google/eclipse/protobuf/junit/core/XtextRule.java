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

import static java.util.Arrays.asList;

import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Injector;
import com.google.inject.Module;

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

  private final CommentReader commentReader;
  private final FileCreator fileCreator;
  private final ProtobufInTestsParser protobufParser;

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
    commentReader = new CommentReader();
    fileCreator = new FileCreator();
    protobufParser = new ProtobufInTestsParser(injector);
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
    for (String comment : commentReader.commentsIn(method)) {
      File protoFile = fileCreator.createFileFrom(comment);
      if (protoFile == null) {
        return comment;
      }
    }
    return null;
  }

  public void parseText(String text) {
    IParseResult parseResult = protobufParser.parseText(text);
    root = (Protobuf) parseResult.getRootASTElement();
    if (root != null) {
      resource = (XtextResource) root.eResource();
    }
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

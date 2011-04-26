/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit;

import java.io.InputStreamReader;

import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.util.StringInputStream;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.eclipse.protobuf.ProtobufStandaloneSetup;
import com.google.eclipse.protobuf.parser.antlr.ProtobufParser;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Injector;

/**
 * Xtext rule that performs basic configuration of an Xtext environment.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class XtextRule implements MethodRule {

  private Injector injector;

  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    return new XtextStatement(base);
  }

  public Injector injector() {
    return injector;
  }

  public <T> T getInstanceOf(Class<T> type) {
    return injector.getInstance(type);
  }

  public Protobuf parse(StringBuilder text) {
    return parse(text.toString());
  }
  
  public Protobuf parse(String text) {
    ProtobufParser parser = injector.getInstance(ProtobufParser.class);
    IParseResult parseResult = parser.parse(new InputStreamReader(new StringInputStream(text)));
    return (Protobuf) parseResult.getRootASTElement();
  }

  private class XtextStatement extends Statement {
    private final Statement base;

    public XtextStatement(Statement base) {
      this.base = base;
    }

    @Override public void evaluate() throws Throwable {
      setUpInjector();
      base.evaluate();
    }

    private void setUpInjector() {
      injector = new ProtobufStandaloneSetup().createInjectorAndDoEMFRegistration();
    }
  }
}

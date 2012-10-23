/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.junit;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class XtextRule implements MethodRule {
  private final Injector injector;

  public XtextRule() {
    this.injector = ProtobufEditorPlugIn.injector();
  }

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    injector.injectMembers(target);
    return base;
  }
}

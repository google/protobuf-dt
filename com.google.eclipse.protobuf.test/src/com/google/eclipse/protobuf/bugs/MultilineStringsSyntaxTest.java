/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.model.util.Syntaxes;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.validation.ProtobufJavaValidator;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test that the parser correctly handles multiline strings.
 */
public class MultilineStringsSyntaxTest {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  @Inject private Syntaxes syntaxes;

  // syntax = "p" // comment inside string
  //          "roto" /* another comment */
  //          "2";
  @Test public void shouldAllowCommentsInsideStrings() {
    boolean isValid = validator.validate(xtext.root(), new BasicDiagnostic(), null);
    assertTrue(isValid);
  }

  // syntax = "p" // comment inside string
  //          "roto" /* another comment */
  //          "2";
  @Test public void shouldUnderstandMultilineStrings() {
    Syntax syntax = xtext.root().getSyntax();
    assertEquals("proto2", syntaxes.getName(syntax));
  }
}

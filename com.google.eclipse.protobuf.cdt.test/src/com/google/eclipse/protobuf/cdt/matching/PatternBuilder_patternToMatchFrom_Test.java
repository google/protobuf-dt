/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.eclipse.protobuf.cdt.matching.PatternMatcher.matches;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.regex.Pattern;

import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.junit.*;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link PatternBuilder#patternToMatchFrom(CppToProtobufMapping)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PatternBuilder_patternToMatchFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private PatternBuilder builder;
  @Inject private IQualifiedNameConverter converter;

  @Test public void should_escape_dots() {
    CppToProtobufMapping mapping = createMessageMapping("com.google.proto.test.Person");
    Pattern pattern = builder.patternToMatchFrom(mapping);
    assertThat(pattern.pattern(), equalTo("com\\.google\\.proto\\.test\\.Person"));
    assertThat("com.google.proto.test.Person", matches(pattern));
  }

  @Test public void should_escape_underscore() {
    CppToProtobufMapping mapping = createMessageMapping("com.google.proto.test.Person_PhoneType");
    Pattern pattern = builder.patternToMatchFrom(mapping);
    assertThat(pattern.pattern(), equalTo("com\\.google\\.proto\\.test\\.Person(\\.|_)PhoneType"));
    assertThat("com.google.proto.test.Person.PhoneType", matches(pattern));
    assertThat("com.google.proto.test.Person_PhoneType", matches(pattern));
  }

  private CppToProtobufMapping createMessageMapping(String qualifiedNameAsText) {
    return new CppToProtobufMapping(converter.toQualifiedName(qualifiedNameAsText), MESSAGE);
  }
}

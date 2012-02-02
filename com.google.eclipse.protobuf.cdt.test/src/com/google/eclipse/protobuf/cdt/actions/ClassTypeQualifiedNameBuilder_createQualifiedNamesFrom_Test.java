/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.cdt.ProtobufCdtModule;
import com.google.eclipse.protobuf.junit.core.*;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ClassTypeQualifiedNameBuilder#createQualifiedNamesFrom(CPPClassType)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ClassTypeQualifiedNameBuilder_createQualifiedNamesFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new ProtobufCdtModule(), new TestModule());

  @Inject private CPPClassType classType;
  @Inject private QualifiedNameFactory qualifiedNameFactory;
  @Inject private ClassTypeQualifiedNameBuilder nameBuilder;

  @Test public void should_return_qualified_names_for_class_type() {
    expectClassTypeToExtendProtoMessage();
    String[] segments = { "com", "google", "proto", "Test" };
    when(classType.getQualifiedName()).thenReturn(segments);
    List<QualifiedName> expected = singletonList(QualifiedName.create(segments));
    when(qualifiedNameFactory.createQualifiedNamesForComplexType(segments)).thenReturn(expected);
    assertThat(nameBuilder.createQualifiedNamesFrom(classType), equalTo(expected));
  }

  private void expectClassTypeToExtendProtoMessage() {
    ICPPBase base = mock(ICPPBase.class);
    when(classType.getBases()).thenReturn(new ICPPBase[] { base });
    when(base.getBaseClassSpecifierName()).thenReturn(createQualifiedName("google", "protobuf", "Message"));
  }

  private CPPASTQualifiedName createQualifiedName(String...segments) {
    CPPASTQualifiedName qualifiedName = new CPPASTQualifiedName();
    for (String segment : segments) {
      qualifiedName.addName(new CASTName(segment.toCharArray()));
    }
    qualifiedName.setFullyQualified(true);
    return qualifiedName;
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(CPPClassType.class);
      mockAndBind(QualifiedNameFactory.class);
    }
  }
}
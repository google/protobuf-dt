/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.fqn;

import static com.google.eclipse.protobuf.cdt.fqn.IsQualifiedNameSource.isQualifiedNameSourceWith;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.cdt.ProtobufCdtModule;
import com.google.eclipse.protobuf.junit.core.*;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ClassTypeQualifiedNameProviderStrategy#qualifiedNamesFrom(IBinding)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ClassTypeQualifiedNameProviderStrategy_qualifiedNamesFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new ProtobufCdtModule(), new TestModule());

  @Inject private CPPClassType classType;
  @Inject private ClassTypeQualifiedNameProviderStrategy nameBuilder;

  @Test public void should_return_qualified_names_for_class_type_if_it_extends_proto_file() {
    expectClassTypeToExtendProtoMessage();
    String[] segments = { "com", "google", "proto", "Test" };
    when(classType.getQualifiedName()).thenReturn(segments);
    Iterable<QualifiedName> qualifiedNames = nameBuilder.qualifiedNamesFrom(classType);
    assertThat(qualifiedNames, isQualifiedNameSourceWith(segments));
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

  @Test public void should_return_null_if_class_type_does_not_extend_proto_message() {
    when(classType.getBases()).thenReturn(new ICPPBase[0]);
    Iterable<QualifiedName> qualifiedNames = nameBuilder.qualifiedNamesFrom(classType);
    assertNull(qualifiedNames);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(CPPClassType.class);
    }
  }
}
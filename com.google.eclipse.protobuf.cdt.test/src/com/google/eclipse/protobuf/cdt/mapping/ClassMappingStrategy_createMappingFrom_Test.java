/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ClassMappingStrategy#createMappingFrom(IBinding)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ClassMappingStrategy_createMappingFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private IBindings bindings;
  @Inject private CPPClassType classType;
  @Inject private ClassMappingStrategy mappingStrategy;

  @Test public void should_create_mapping_if_class_type_is_message() {
    when(bindings.isMessage(classType)).thenReturn(true);
    List<String> segments = newArrayList("com", "google", "proto", "Test");
    when(bindings.qualifiedNameOf(classType)).thenReturn(segments);
    CppToProtobufMapping mapping = mappingStrategy.createMappingFrom(classType);
    assertThat(mapping.qualifiedName(), equalTo(segments));
    assertThat(mapping.type(), equalTo(MESSAGE));
  }

  @Test public void should_return_null_if_class_type_is_not_message() {
    when(bindings.isMessage(classType)).thenReturn(false);
    CppToProtobufMapping mapping = mappingStrategy.createMappingFrom(classType);
    assertNull(mapping);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(IBindings.class);
      mockAndBind(CPPClassType.class);
    }
  }
}
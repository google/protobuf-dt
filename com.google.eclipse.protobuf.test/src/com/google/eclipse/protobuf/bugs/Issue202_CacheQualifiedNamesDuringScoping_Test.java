/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.eclipse.xtext.util.Tuples.pair;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.naming.IProtobufQualifiedNameProvider;
import com.google.eclipse.protobuf.naming.NameType;
import com.google.eclipse.protobuf.naming.NamingStrategy;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=202">Issue 202</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue202_CacheQualifiedNamesDuringScoping_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IProtobufQualifiedNameProvider provider;

  private NamingStrategy namingStrategy;

  @Before public void setUp() {
    namingStrategy = mock(NamingStrategy.class);
  }

  // syntax = "proto2";
  //
  // package fqn.test;
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_cache_qualified_names() {
    Message message = xtext.find("Person", Message.class);
    when(namingStrategy.nameOf(message)).thenReturn(pair(NameType.NORMAL, "Person"));
    QualifiedName qualifiedName = provider.getFullyQualifiedName(message, namingStrategy);
    assertThat(qualifiedName.toString(), equalTo("fqn.test.Person"));
    for (int i = 0; i < 10; i++) {
      // these calls should hit the cache
      assertSame(qualifiedName, provider.getFullyQualifiedName(message, namingStrategy));
    }
    verify(namingStrategy, times(1)).nameOf(message);
  }
}

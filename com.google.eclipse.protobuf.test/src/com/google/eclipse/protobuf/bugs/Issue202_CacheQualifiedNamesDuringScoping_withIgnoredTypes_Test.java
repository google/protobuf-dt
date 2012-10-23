/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.naming.IProtobufQualifiedNameProvider;
import com.google.eclipse.protobuf.naming.NamingStrategy;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.BooleanLink;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.NumberLink;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ScalarTypeLink;
import com.google.eclipse.protobuf.protobuf.StringLink;
import com.google.eclipse.protobuf.protobuf.ValueField;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=202">Issue 202</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@RunWith(Parameterized.class)
public class Issue202_CacheQualifiedNamesDuringScoping_withIgnoredTypes_Test {

  @Parameters
  public static Collection<Object[]> parameters() {
    return asList(new Object[][] {
        { Protobuf.class }, { Import.class }, { AbstractOption.class }, { OptionSource.class },
        { ScalarTypeLink.class }, { NumberLink.class }, { BooleanLink.class }, { StringLink.class },
        { ComplexValue.class }, { ValueField.class }, { FieldName.class }
      });
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IProtobufQualifiedNameProvider provider;

  private NamingStrategy namingStrategy;

  private final EObject ignored;

  public Issue202_CacheQualifiedNamesDuringScoping_withIgnoredTypes_Test(Class<EObject> ignoredType) {
    ignored = mock(ignoredType);
  }

  @Before public void setUp() {
    namingStrategy = mock(NamingStrategy.class);
  }

  @Test public void should_not_cache_objects_of_ignored_type() {
    assertNull(provider.getFullyQualifiedName(ignored, namingStrategy));
    verifyZeroInteractions(namingStrategy);
  }
}

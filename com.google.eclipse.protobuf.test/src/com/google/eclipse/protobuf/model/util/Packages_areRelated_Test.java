/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Packages#areRelated(Package, Package)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Packages_areRelated_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Packages packages;

  private String baseName;
  private String[] subpackageNames;
  private Package p1;
  private Package p2;

  @Before public void setUp() {
    baseName = "may.the.force.be.with.you";
    subpackageNames = new String[] {
        "may.the.force.be.with",
        "may.the.force.be.",
        "may.the.force.",
        "may.the",
        "may"
    };
    p1 = mock(Package.class);
    when(p1.getName()).thenReturn(baseName);
    p2 = mock(Package.class);
  }

  @Test public void should_return_true_if_packages_are_equal() {
    when(p2.getName()).thenReturn(baseName);
    assertTrue(packages.areRelated(p1, p2));
  }

  @Test public void should_return_true_second_is_subPackage_of_first() {
    for (String name : subpackageNames) {
      when(p2.getName()).thenReturn(name);
      assertTrue(packages.areRelated(p1, p2));
    }
  }

  @Test public void should_return_true_first_is_subPackage_of_second() {
    for (String name : subpackageNames) {
      when(p2.getName()).thenReturn(name);
      assertTrue(packages.areRelated(p2, p1));
    }
  }

  @Test public void should_return_false_if_second_starts_with_few_segments_of_first_but_is_not_subpackage() {
    when(p2.getName()).thenReturn("may.the.ring");
    assertFalse(packages.areRelated(p1, p2));
  }

  @Test public void should_return_false_if_names_are_completely_different() {
    when(p2.getName()).thenReturn("peace.dog");
    assertFalse(packages.areRelated(p1, p2));
  }
}

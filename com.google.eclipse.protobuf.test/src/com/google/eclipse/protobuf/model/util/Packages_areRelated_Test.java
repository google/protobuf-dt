/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.*;
import org.junit.*;

import java.util.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link Packages#areRelated(Package, Package)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
// TODO reimplement
public class Packages_areRelated_Test {

//  private static Packages packages;
//
//  @BeforeClass public static void setUpOnce() {
//    packages = new Packages();
//  }
//
//  private EList<String> baseName;
//  private List<BasicEList<String>> subpackageNames;
//  private Package p1;
//  private Package p2;
//  
//  @Before public void setUp() {
//    baseName = new BasicEList<String>(asList("may", "the", "force", "be", "with", "you"));
//    subpackageNames = asList(
//        new BasicEList<String>(asList("may", "the", "force", "be", "with")),
//        new BasicEList<String>(asList("may", "the", "force", "be")),
//        new BasicEList<String>(asList("may", "the", "force")),
//        new BasicEList<String>(asList("may", "the")),
//        new BasicEList<String>(asList("may"))
//    );
//    p1 = mock(Package.class);
//    when(p1.getSegments()).thenReturn(baseName);
//    p2 = mock(Package.class);
//  }
//
//  @Test public void should_return_true_if_packages_are_equal() {
//    when(p2.getName()).thenReturn(baseName);
//    assertTrue(packages.areRelated(p1, p2));
//  }
//
//  @Test public void should_return_true_second_is_subPackage_of_first() {
//    for (String name : subpackageNames) {
//      when(p2.getName()).thenReturn(name);
//      assertTrue(packages.areRelated(p1, p2));
//    }
//  }
//
//  @Test public void should_return_true_first_is_subPackage_of_second() {
//    for (String name : subpackageNames) {
//      when(p2.getName()).thenReturn(name);
//      assertTrue(packages.areRelated(p2, p1));
//    }
//  }
//
//  @Test public void should_return_false_if_second_starts_with_few_segments_of_first_but_is_not_subpackage() {
//    when(p2.getName()).thenReturn("may.the.ring");
//    assertFalse(packages.areRelated(p1, p2));
//  }
//
//  @Test public void should_return_false_if_names_are_completely_different() {
//    when(p2.getName()).thenReturn("peace.dog");
//    assertFalse(packages.areRelated(p1, p2));
//  }
}

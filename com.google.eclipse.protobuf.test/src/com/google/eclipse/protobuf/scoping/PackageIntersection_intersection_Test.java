/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Package;

import org.junit.*;

import java.util.List;

/**
 * Tests for <code>{@link PackageIntersection#intersection(Package, Package)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PackageIntersection_intersection_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Package p1;
  private Package p2;
  private PackageIntersection intersection;

  @Before public void setUp() {
    p1 = mock(Package.class);
    p2 = mock(Package.class);
    intersection = xtext.getInstanceOf(PackageIntersection.class);
  }

  @Test public void should_return_intersection_when_initial_segments_are_equal() {
    when(p1.getName()).thenReturn("com.google.proto.test.project.shared");
    when(p2.getName()).thenReturn("com.google.proto.test.base.shared");
    List<String> segments = intersection.intersection(p1, p2);
    assertThat(segments, equalTo(asList("base", "shared")));
  }

  @Test public void should_return_empty_list_when_packages_are_different() {
    when(p1.getName()).thenReturn("project.shared");
    when(p2.getName()).thenReturn("base.shared");
    List<String> segments = intersection.intersection(p1, p2);
    assertTrue(segments.isEmpty());
  }

  @Test public void should_return_empty_list_when_packages_are_exactly_equal() {
    when(p1.getName()).thenReturn("project.shared");
    when(p2.getName()).thenReturn("project.shared");
    List<String> segments = intersection.intersection(p1, p2);
    assertTrue(segments.isEmpty());
  }
}

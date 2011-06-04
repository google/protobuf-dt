/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.stubs.protobuf.PackageStub;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link PackageResolver#areRelated(Package, Package)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PackageResolver_areRelated_Test {

  private static PackageResolver resolver;

  @BeforeClass public static void setUpOnce() {
    resolver = new PackageResolver();
  }

  private Package p1;
  private Package p2;

  @Before public void setUp() {
    p1 = new PackageStub("may.the.force.be.with.you");
    p2 = new PackageStub();
  }

  @Test public void should_return_true_if_packages_are_equal() {
    p2.setName(p1.getName());
    assertThat(resolver.areRelated(p1, p2), equalTo(true));
  }
}

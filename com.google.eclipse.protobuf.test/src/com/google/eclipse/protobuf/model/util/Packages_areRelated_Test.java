/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.stubs.protobuf.PackageStub;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link Packages#areRelated(Package, Package)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Packages_areRelated_Test {

  private static Packages packages;

  @BeforeClass public static void setUpOnce() {
    packages = new Packages();
  }

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
    p1 = new PackageStub(baseName);
    p2 = new PackageStub();
  }

  @Test public void should_return_true_if_packages_are_equal() {
    p2.setName(p1.getName());
    assertThat(packages.areRelated(p1, p2), equalTo(true));
  }

  @Test public void should_return_true_second_is_subPackage_of_first() {
    for (String name : subpackageNames) {
      p2.setName(name);
      assertThat(packages.areRelated(p1, p2), equalTo(true));
    }
  }

  @Test public void should_return_true_first_is_subPackage_of_second() {
    p2.setName(baseName);
    for (String name : subpackageNames) {
      p1.setName(name);
      assertThat(packages.areRelated(p1, p2), equalTo(true));
    }
  }

  @Test public void should_return_false_if_second_starts_with_few_segments_of_first_but_is_not_subpackage() {
    p2.setName("may.the.ring");
    assertThat(packages.areRelated(p1, p2), equalTo(false));
  }

  @Test public void should_return_false_if_names_are_completely_different() {
    p2.setName("peace.dog");
    assertThat(packages.areRelated(p1, p2), equalTo(false));
  }
}

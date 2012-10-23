/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.model.util.QualifiedNameCollectionContains.contains;

import java.util.Collection;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Packages#addPackageNameSegments(Package, QualifiedName)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Packages_addPackageNameSegments_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Packages packages;


  // syntax = "proto2";
  //
  // package com.google.test;
  @Test public void should_create_a_qualified_name_per_segment_in_package_name() {
    Package aPackage = xtext.find("com.google.test", Package.class);
    Collection<QualifiedName> names = packages.addPackageNameSegments(aPackage, QualifiedName.create("Person"));
    assertThat(names, contains("test.Person", "google.test.Person"));
  }

  // syntax = "proto2";
  //
  // package google;
  @Test public void should_return_empty_list_if_package_has_only_one_segment() {
    Package aPackage = xtext.find("google", Package.class);
    Collection<QualifiedName> names = packages.addPackageNameSegments(aPackage, QualifiedName.create("Person"));
    assertTrue(names.isEmpty());
  }
}

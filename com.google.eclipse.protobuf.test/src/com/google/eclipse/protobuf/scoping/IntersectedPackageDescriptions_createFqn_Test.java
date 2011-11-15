/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import java.util.List;

/**
 * Tests for <code>{@link IntersectedPackageDescriptions#createFqn(String, List)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IntersectedPackageDescriptions_createFqn_Test {

  private String name;
  private List<String> packageFqn;
  private IntersectedPackageDescriptions descriptions;
  
  @Before public void setUp() {
    name = "Person";
    packageFqn = asList("com", "google", "test");
    descriptions = new IntersectedPackageDescriptions();
  }
  
  @Test public void should_concatenate_name_and_package_fqn() {
    QualifiedName fqn = descriptions.createFqn(name, packageFqn);
    assertThat(fqn.toString(), equalTo("com.google.test.Person"));
  }
}

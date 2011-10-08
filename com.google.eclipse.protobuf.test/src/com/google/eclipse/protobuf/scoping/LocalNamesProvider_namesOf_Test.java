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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import java.util.List;

/**
 * Tests for <code>{@link LocalNamesProvider#namesOf(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LocalNamesProvider_namesOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private LocalNamesProvider namesProvider;

  @Before public void setUp() {
    namesProvider = xtext.getInstanceOf(LocalNamesProvider.class);
  }

  // package test.alternative.names;
  // message Person {
  //   message PhoneNumber {
  //     optional PhoneType type = 1 [default = HOME];
  //     enum PhoneType {
  //       HOME = 0;
  //       WORK = 1;
  //     }
  //   }
  // }
  @Test public void should_return_all_possible_local_names() {
    Enum phoneType = xtext.find("PhoneType", " {", Enum.class);
    List<QualifiedName> names = namesProvider.namesOf(phoneType);
    assertThat(names.get(0).toString(), equalTo("PhoneType"));
    assertThat(names.get(1).toString(), equalTo("PhoneNumber.PhoneType"));
    assertThat(names.get(2).toString(), equalTo("Person.PhoneNumber.PhoneType"));
    assertThat(names.get(3).toString(), equalTo("names.Person.PhoneNumber.PhoneType"));
    assertThat(names.get(4).toString(), equalTo("alternative.names.Person.PhoneNumber.PhoneType"));
  }
}

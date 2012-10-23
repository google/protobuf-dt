/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link LocalNamesProvider#localNames(EObject, NamingStrategy)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LocalNamesProvider_getAllLocalNames_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private LocalNamesProvider namesProvider;
  @Inject private NormalNamingStrategy normalNamingStrategy;

  // syntax = "proto2";
  //
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
    List<QualifiedName> names = namesProvider.localNames(phoneType, normalNamingStrategy);
    assertThat(names.get(0).toString(), equalTo("PhoneType"));
    assertThat(names.get(1).toString(), equalTo("PhoneNumber.PhoneType"));
    assertThat(names.get(2).toString(), equalTo("Person.PhoneNumber.PhoneType"));
    assertThat(names.get(3).toString(), equalTo("names.Person.PhoneNumber.PhoneType"));
    assertThat(names.get(4).toString(), equalTo("alternative.names.Person.PhoneNumber.PhoneType"));
  }
}

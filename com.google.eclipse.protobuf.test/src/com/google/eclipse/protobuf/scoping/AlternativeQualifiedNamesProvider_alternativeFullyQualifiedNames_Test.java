/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.util.Finder.findEnum;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * Tests for <code>{@link AlternativeQualifiedNamesProvider#alternativeFullyQualifiedNames(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class AlternativeQualifiedNamesProvider_alternativeFullyQualifiedNames_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private AlternativeQualifiedNamesProvider namesProvider;

  @Before public void setUp() {
    namesProvider = xtext.getInstanceOf(AlternativeQualifiedNamesProvider.class);
  }

  @Test public void should_return_all_possible_qualified_names() {
    StringBuilder proto = new StringBuilder();
    proto.append("package alternative.names;                       ");
    proto.append("                                                 ");
    proto.append("message Person {                                 ");
    proto.append("  message PhoneNumber {                          ");
    proto.append("    optional PhoneType type = 1 [default = HOME];");
    proto.append("                                                 ");
    proto.append("    enum PhoneType {                             ");
    proto.append("      HOME = 0;                                  ");
    proto.append("      WORK = 1;                                  ");
    proto.append("    }                                            ");
    proto.append(" }                                               ");
    proto.append("}                                                ");
    Protobuf root = xtext.parse(proto);
    Enum phoneType = findEnum("PhoneType", root);
    List<QualifiedName> names = namesProvider.alternativeFullyQualifiedNames(phoneType);
    assertThat(names.get(0).toString(), equalTo("PhoneType"));
    assertThat(names.get(1).toString(), equalTo("PhoneNumber.PhoneType"));
    assertThat(names.get(2).toString(), equalTo("Person.PhoneNumber.PhoneType"));
  }
}

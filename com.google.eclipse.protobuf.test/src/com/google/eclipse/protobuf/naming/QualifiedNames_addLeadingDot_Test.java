/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.naming.QualifiedNames;

/**
 * Tests for <code>{@link QualifiedNames#addLeadingDot(QualifiedName)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class QualifiedNames_addLeadingDot_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private QualifiedNames qualifiedNames;

  @Before public void setUp() {
    qualifiedNames = xtext.getInstanceOf(QualifiedNames.class);
  }

  @Test public void should_add_leading_dot() {
    QualifiedName name = QualifiedName.create("jedis", "Luke");
    QualifiedName withLeadingDot = qualifiedNames.addLeadingDot(name);
    assertThat(withLeadingDot.toString(), equalTo(".jedis.Luke"));
  }

  @Test public void should_not_add_leading_dot_if_qualified_name_already_has_it() {
    QualifiedName name = QualifiedName.create("", "jedis", "Luke");
    QualifiedName withLeadingDot = qualifiedNames.addLeadingDot(name);
    assertThat(withLeadingDot.toString(), equalTo(".jedis.Luke"));
  }
}

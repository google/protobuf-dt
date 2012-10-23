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

import org.eclipse.xtext.naming.QualifiedName;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.model.util.QualifiedNames;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link QualifiedNames#addLeadingDot(QualifiedName)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class QualifiedNames_addLeadingDot_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private QualifiedNames qualifiedNames;

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

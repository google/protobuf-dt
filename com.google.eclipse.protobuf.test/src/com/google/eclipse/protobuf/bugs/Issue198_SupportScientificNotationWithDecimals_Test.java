/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.DoubleLink;
import com.google.eclipse.protobuf.protobuf.Value;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=198">Issue 198</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue198_SupportScientificNotationWithDecimals_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message Test {
  //  optional double timeout = 1 [default = 1.0e7];
  // }
  @Test public void should_not_have_syntax_errors() {
    DefaultValueFieldOption option = xtext.find("default", DefaultValueFieldOption.class);
    Value value = option.getValue();
    assertThat(value, instanceOf(DoubleLink.class));
    DoubleLink link = (DoubleLink) value;
    assertThat(link.getTarget(), equalTo(1.0E7d));
  }
}

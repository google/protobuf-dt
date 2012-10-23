/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.DoubleLink;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=166">Issue 166</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue166_SupportOptionalDecimals_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message Test {
  //   optional double something = 1 [default = 1.];
  // }
  @Test public void should_support_optional_decimals() {
    DefaultValueFieldOption option = xtext.find("default", DefaultValueFieldOption.class);
    DoubleLink link = (DoubleLink) option.getValue();
    assertThat(link.getTarget(), equalTo(1.0d));
  }
}

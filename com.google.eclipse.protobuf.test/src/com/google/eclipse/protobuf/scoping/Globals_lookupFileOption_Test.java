/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;

/**
 * Tests for <code>{@link Globals#lookupFileOption(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Globals_lookupFileOption_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private Globals globals;
  
  @Before public void setUp() {
    globals = xtext.getInstanceOf(Globals.class);
  }

  @Test public void should_look_up_file_option() {
    Property option = globals.lookupFileOption("java_multiple_files");
    assertThat(option.getName(), equalTo("java_multiple_files"));
  }
  
  @Test public void should_return_null_if_option_not_found() {
    assertThat(globals.lookupFileOption("hello"), nullValue());
  }
}

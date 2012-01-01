/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link CommaSeparatedValues#splitCsv(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommaSeparatedValues_splitCsv_Test {
  @Test public void should_split_CSV() {
    String[] values = CommaSeparatedValues.splitCsv("Yoda, Leia, Luke");
    assertThat(values, equalTo(new String[] { "Yoda", "Leia", "Luke" }));
  }

  @Test public void should_return_same_String_if_it_is_not_CSV() {
    String[] values = CommaSeparatedValues.splitCsv("Yoda");
    assertThat(values, equalTo(new String[] { "Yoda" }));
  }
}

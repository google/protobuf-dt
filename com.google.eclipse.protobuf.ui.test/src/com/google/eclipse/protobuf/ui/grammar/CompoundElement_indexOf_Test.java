/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.EQUAL;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.TRUE;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL;

import org.junit.Test;

import com.google.eclipse.protobuf.grammar.CommonKeyword;

/**
 * Tests for <code>{@link CompoundElement#indexOf(CommonKeyword)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompoundElement_indexOf_Test {
  @Test public void should_return_index_of_keyword_as_substring() {
    assertThat(DEFAULT_EQUAL.indexOf(EQUAL), equalTo(DEFAULT_EQUAL.toString().indexOf(EQUAL.toString())));
  }

  @Test public void should_return_negative_one_if_keyword_not_found() {
    assertThat(DEFAULT_EQUAL.indexOf(TRUE), equalTo(-1));
  }
}

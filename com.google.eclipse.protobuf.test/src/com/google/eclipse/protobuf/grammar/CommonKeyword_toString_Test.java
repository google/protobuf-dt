/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.grammar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.BOOL;

import org.junit.Test;

/**
 * Tests for <code>{@link CommonKeyword#toString()}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommonKeyword_toString_Test {
  @Test public void should_return_value() {
    assertThat(BOOL.toString(), equalTo("bool"));
  }
}

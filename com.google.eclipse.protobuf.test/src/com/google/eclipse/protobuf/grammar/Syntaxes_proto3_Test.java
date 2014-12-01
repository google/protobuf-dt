/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.grammar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link Syntaxes#proto3()}</code>.
 */
public class Syntaxes_proto3_Test {
  @Test public void should_return_proto3() {
    assertThat(Syntaxes.proto3(), equalTo("proto3"));
  }
}

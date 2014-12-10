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

import com.google.eclipse.protobuf.model.util.Syntaxes;

import org.junit.Test;

/**
 * Tests for <code>{@link Syntaxes#PROTO2}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Syntaxes_proto2_Test {
  @Test public void should_return_proto2() {
    assertThat(Syntaxes.PROTO2, equalTo("proto2"));
  }
}

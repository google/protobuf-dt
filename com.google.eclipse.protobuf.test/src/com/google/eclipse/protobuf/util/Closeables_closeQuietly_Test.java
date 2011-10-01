/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.*;

/**
 * Tests for <code>{@link Closeables#closeQuietly(Closeable)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Closeables_closeQuietly_Test {

  private Closeable closeable;

  @Before public void setUp() {
    closeable = mock(Closeable.class);
  }

  @Test public void should_ignore_null_Closeable() {
    boolean result = Closeables.closeQuietly(null);
    assertThat(result, equalTo(false));
  }

  @Test public void should_close_Closeable() throws IOException {
    boolean result = Closeables.closeQuietly(closeable);
    verify(closeable).close();
    assertThat(result, equalTo(true));
  }

  @Test public void should_ignore_exceptions_thrown_when_closing_Closeable() throws IOException {
    doThrow(new RuntimeException()).when(closeable).close();
    boolean result = Closeables.closeQuietly(closeable);
    verify(closeable).close();
    assertThat(result, equalTo(true));
  }
}

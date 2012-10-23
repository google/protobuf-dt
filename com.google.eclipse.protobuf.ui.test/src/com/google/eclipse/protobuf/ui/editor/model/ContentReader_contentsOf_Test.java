/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.xtext.util.StringInputStream;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ContentReader#contentsOf(InputStream)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContentReader_contentsOf_Test {
  private ContentReader reader;

  @Before public void setUp() {
    reader = new ContentReader();
  }

  @Test public void should_read_InputStream() throws IOException {
    String contents = "Hello \r\n World";
    InputStream input = new StringInputStream(contents);
    assertThat(reader.contentsOf(input), equalTo(contents));
  }
}

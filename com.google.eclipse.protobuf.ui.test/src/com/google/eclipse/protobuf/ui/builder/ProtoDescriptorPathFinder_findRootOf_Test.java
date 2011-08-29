/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import org.junit.*;
import org.junit.rules.ExpectedException;

/**
 * Tests for <code>{@link ProtoDescriptorPathFinder#findRootOf(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtoDescriptorPathFinder_findRootOf_Test {

  private static ProtoDescriptorPathFinder finder;

  @BeforeClass public static void setUpOnce() {
    finder = new ProtoDescriptorPathFinder();
  }

  @Rule public ExpectedException thrown = none();

  @Test public void should_return_null_if_path_is_null() {
    assertThat(finder.findRootOf(null), nullValue());
  }

  @Test public void should_throw_error_if_path_does_not_contain_descriptor_FQN() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Path '/usr/local/include' does not contain '/google/protobuf/descriptor.proto'");
    finder.findRootOf("/usr/local/include");
  }

  @Test public void should_find_import_root_of_descriptor() {
    String filePath = "/usr/local/include/google/protobuf/descriptor.proto";
    assertThat(finder.findRootOf(filePath), equalTo("/usr/local/include"));
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.URI;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Uris#prefixOf(URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Uris_prefixOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Uris uris;

  @Test public void should_return_prefix_of_file_URI() {
    URI uri = URI.createURI("file:/usr/local/project/src/protos/test.proto");
    assertThat(uris.prefixOf(uri), equalTo("file:"));
  }

  @Test public void should_return_prefix_of_platform_resource_URI() {
    URI uri = URI.createURI("platform:/resource/project/src/protos/test.proto");
    assertThat(uris.prefixOf(uri), equalTo("platform:/resource"));
  }
}

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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Uris#segmentsWithoutFileName(URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Uris_segmentsWithoutFileName_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Uris uris;

  @Test public void should_remove_last_segment_if_URI_refers_to_file() {
    URI uri = URI.createURI("file:/usr/local/project/src/protos/test.proto");
    List<String> expected = newArrayList("usr", "local", "project", "src", "protos");
    assertThat(uris.segmentsWithoutFileName(uri), equalTo(expected));
  }

  @Test public void should_remove_first_and_last_segments_if_URI_refers_to_platform_resource() {
    URI uri = URI.createURI("platform:/resource/project/src/protos/test.proto");
    List<String> expected = newArrayList("project", "src", "protos");
    assertThat(uris.segmentsWithoutFileName(uri), equalTo(expected));
  }
}

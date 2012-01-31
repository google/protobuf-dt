/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.eclipse.emf.common.util.URI;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Uris#exists(URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Uris_exists_withPlatformResource_Test {
  private static URI resourceUri;

  @BeforeClass public static void setUpOnce() {
    resourceUri = URI.createURI("platform:/resource/project/src/protos/test.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private Resources resources;
  @Inject private Uris uris;

  @Test public void should_return_true_if_platform_resource_exists() {
    when(resources.fileExists(resourceUri)).thenReturn(true);
    assertTrue(uris.exists(resourceUri));
  }

  @Test public void should_return_false_if_platform_resource_does_not_exist() {
    when(resources.fileExists(resourceUri)).thenReturn(false);
    assertFalse(uris.exists(resourceUri));
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(Resources.class);
    }
  }
}

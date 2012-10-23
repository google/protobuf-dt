/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ModelObjects#uriOf(EObject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjects_uriOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ModelObjects objects;

  // syntax = "proto2";
  //
  // message Person {}
  @Test public void should_return_uri_of_model_object() {
    Message message = xtext.find("Person", Message.class);
    URI expected = xtext.resource().getURI();
    expected = expected.appendFragment(xtext.resource().getURIFragment(message));
    assertThat(objects.uriOf(message), equalTo(expected));
  }
}

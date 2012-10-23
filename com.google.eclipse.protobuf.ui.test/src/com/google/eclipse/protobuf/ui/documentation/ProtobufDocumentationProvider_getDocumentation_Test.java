/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufDocumentationProvider#getDocumentation(EObject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentationProvider_getDocumentation_Test {
  private static EObject o;

  @BeforeClass public static void setUpOnce() {
    o = mock(EObject.class);
  }

  private SLCommentDocumentationProvider delegate1;
  private MLCommentDocumentationProvider delegate2;

  private ProtobufDocumentationProvider provider;

  @Before public void setUp() {
    delegate1 = mock(SLCommentDocumentationProvider.class);
    delegate2 = mock(MLCommentDocumentationProvider.class);
    provider = new ProtobufDocumentationProvider(delegate1, delegate2);
  }

  @Test public void should_use_first_delegate_if_it_returns_comment() {
    String comment = "Hello World";
    when(delegate1.getDocumentation(o)).thenReturn(comment);
    assertThat(provider.getDocumentation(o), equalTo(comment));
    verify(delegate1).getDocumentation(o);
    verifyZeroInteractions(delegate2);
  }

  @Test public void should_use_second_delegage_if_first_does_not_return_comment() {
    when(delegate1.getDocumentation(o)).thenReturn("");
    String comment = "Hello World";
    when(delegate2.getDocumentation(o)).thenReturn(comment);
    assertThat(provider.getDocumentation(o), equalTo(comment));
    verifyAllDelegatesWereInvoked();
  }

  @Test public void should_return_empty_String_if_delegates_do_not_return_comment() {
    when(delegate1.getDocumentation(o)).thenReturn("");
    when(delegate2.getDocumentation(o)).thenReturn("");
    assertThat(provider.getDocumentation(o), equalTo(""));
    verifyAllDelegatesWereInvoked();
  }

  private void verifyAllDelegatesWereInvoked() {
    verify(delegate1).getDocumentation(o);
    verify(delegate2).getDocumentation(o);
  }
}

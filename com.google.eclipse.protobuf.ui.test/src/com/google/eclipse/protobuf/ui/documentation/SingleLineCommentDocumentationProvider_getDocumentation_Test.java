/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static com.google.eclipse.protobuf.junit.util.Finder.findProperty;
import static com.google.eclipse.protobuf.junit.util.SystemProperties.lineSeparator;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

/**
 * Tests for <code>{@link SingleLineCommentDocumentationProvider#getDocumentation(EObject)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleLineCommentDocumentationProvider_getDocumentation_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private SingleLineCommentDocumentationProvider provider;
  
  @Before public void setUp() {
    provider = xtext.getInstanceOf(SingleLineCommentDocumentationProvider.class);
  }
  
  @Test public void should_return_single_line_comment_of_element() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ").append(lineSeparator())
         .append("  // Indicates whether the person is active or not.").append(lineSeparator())
         .append("  optional bool active = 1;").append(lineSeparator())
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    String documentation = provider.getDocumentation(active);
    assertThat(documentation, equalTo("Indicates whether the person is active or not."));
  }
  
  @Test public void should_return_empty_String_if_element_does_not_have_single_line_comment() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    String documentation = provider.getDocumentation(active);
    assertThat(documentation, equalTo(""));
  }
}

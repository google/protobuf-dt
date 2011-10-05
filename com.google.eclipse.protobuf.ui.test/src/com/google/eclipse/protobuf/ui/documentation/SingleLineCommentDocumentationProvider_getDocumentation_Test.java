/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link SingleLineCommentDocumentationProvider#getDocumentation(EObject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleLineCommentDocumentationProvider_getDocumentation_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private SingleLineCommentDocumentationProvider provider;

  @Before public void setUp() {
    provider = xtext.getInstanceOf(SingleLineCommentDocumentationProvider.class);
  }

  @Test public void should_return_single_line_comment_of_element() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                                   ")
         .append("  // Indicates whether the person is active or not.")
         .append("  optional bool active = 1;                        ")
         .append("}                                                  ");
    Protobuf root = xtext.parseText(proto);
    Property active = findProperty(name("active"), in(root));
    String documentation = provider.getDocumentation(active);
    assertThat(documentation, equalTo("Indicates whether the person is active or not."));
  }

  @Test public void should_return_empty_String_if_element_does_not_have_single_line_comment() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property active = findProperty(name("active"), in(root));
    String documentation = provider.getDocumentation(active);
    assertThat(documentation, equalTo(""));
  }
}

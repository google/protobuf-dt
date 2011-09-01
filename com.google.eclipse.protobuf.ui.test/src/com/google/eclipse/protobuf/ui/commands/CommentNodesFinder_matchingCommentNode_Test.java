/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.junit.util.Finder.findProperty;
import static com.google.eclipse.protobuf.junit.util.SystemProperties.lineSeparator;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;

import java.util.regex.Pattern;

/**
 * Tests for <code>{@link CommentNodesFinder#matchingCommentNode(EObject, Pattern...)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentNodesFinder_matchingCommentNode_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private CommentNodesFinder finder;
  
  @Before public void setUp() {
    finder = xtext.getInstanceOf(CommentNodesFinder.class);
  }
  
  @Test public void should_return_matching_single_line_comment_of_element() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ").append(lineSeparator())
         .append("  // Indicates whether the person is active or not.").append(lineSeparator())
         .append("  optional bool active = 1;").append(lineSeparator())
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    INode node = finder.matchingCommentNode(active, Pattern.compile(".*"));
    assertThat(node.getText().trim(), equalTo("// Indicates whether the person is active or not."));
  }

  @Test public void should_return_matching_multi_line_comment_of_element() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ").append(lineSeparator())
         .append("  /*").append(lineSeparator())
         .append("   * Indicates whether the person is active or not.").append(lineSeparator())
         .append("   */").append(lineSeparator())
         .append("  optional bool active = 1;").append(lineSeparator())
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    INode node = finder.matchingCommentNode(active, Pattern.compile(".*"));
    assertThat(node, notNullValue());
  }
}

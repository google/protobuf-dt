/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Pair;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link CommentNodesFinder#matchingCommentNode(EObject, String...)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentNodesFinder_matchingCommentNode_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private CommentNodesFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(CommentNodesFinder.class);
  }

  @Test public void should_return_matching_single_line_comment_of_element() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  // Next Id: 6            ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property active = findProperty(name("active"), in(root));
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "next id: [\\d]+");
    INode node = match.getFirst();
    assertThat(node.getText().trim(), equalTo("// Next Id: 6"));
  }

  @Test public void should_return_matching_multi_line_comment_of_element() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  /*                       ")
         .append("   * Next Id: 6            ")
         .append("   */                      ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property active = findProperty(name("active"), in(root));
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "NEXT ID: [\\d]+");
    INode node = match.getFirst();
    assertThat(node, notNullValue());
  }

  @Test public void should_return_null_if_no_matching_node_found() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  // Next Id: 6            ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property active = findProperty(name("active"), in(root));
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "Hello");
    assertThat(match, nullValue());
  }
}

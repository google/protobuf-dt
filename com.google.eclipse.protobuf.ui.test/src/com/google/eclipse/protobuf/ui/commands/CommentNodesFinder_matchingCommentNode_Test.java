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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Pair;
import org.junit.*;

import java.util.regex.Matcher;

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

  // message Person {
  //   // Next Id: 6
  //   optional bool active = 1;
  // }
  @Test public void should_return_matching_single_line_comment_of_element() {
    Property active = xtext.find("active", Property.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "next id: [\\d]+");
    INode node = match.getFirst();
    assertThat(node.getText().trim(), equalTo("// Next Id: 6"));
  }

  // message Person {
  //   /*
  //    * Next Id: 6
  //    */
  //   optional bool active = 1;
  // }
  @Test public void should_return_matching_multi_line_comment_of_element() {
    Property active = xtext.find("active", Property.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "NEXT ID: [\\d]+");
    INode node = match.getFirst();
    assertThat(node, notNullValue());
  }

  // message Person {
  //   // Next Id: 6
  //   optional bool active = 1;
  // }
  @Test public void should_return_null_if_no_matching_node_found() {
    Property active = xtext.find("active", Property.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "Hello");
    assertThat(match, nullValue());
  }
}

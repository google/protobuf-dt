/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands.semicolon;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.regex.Matcher;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Pair;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link CommentNodesFinder#matchingCommentNode(EObject, String...)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentNodesFinder_matchingCommentNode_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private CommentNodesFinder finder;

  // syntax = "proto2";
  //
  // message Person {
  //   // Next Id: 6
  //   optional bool active = 1;
  // }
  @Test public void should_return_matching_single_line_comment_of_element() {
    MessageField field = xtext.find("active", MessageField.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(field, "next id: [\\d]+");
    INode node = match.getFirst();
    assertThat(node.getText().trim(), equalTo("// Next Id: 6"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   /*
  //    * Next Id: 6
  //    */
  //   optional bool active = 1;
  // }
  @Test public void should_return_matching_multi_line_comment_of_element() {
    MessageField field = xtext.find("active", MessageField.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(field, "NEXT ID: [\\d]+");
    assertNotNull(match.getFirst());
  }

  // syntax = "proto2";
  //
  // message Person {
  //   // Next Id: 6
  //   optional bool active = 1;
  // }
  @Test public void should_return_null_if_no_matching_node_found() {
    MessageField active = xtext.find("active", MessageField.class);
    Pair<INode, Matcher> match = finder.matchingCommentNode(active, "Hello");
    assertNull(match);
  }
}

/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands.semicolon;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Inject;

public class SmartSemicolonHandlerTest {
  @Rule
  public XtextRule xtext = XtextRule.createWith(ProtobufEditorPlugIn.injector());

  @Inject
  private SmartSemicolonHandler handler;

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool incomplete
  // }
  @Test public void shouldDetermineFirstIndexToBe1() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(1L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool in_message = 2;
  //   optional group outer_group = 4 {
  //     optional bool in_outer_group = 5;
  //     optional group inner_group = 3 {
  //       optional bool in_inner_group = 1;
  //       optional bool incomplete
  //     }
  //   }
  // }
  @Test public void shouldDetermineCorrectIndexInsideOfGroups() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(6L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool in_message = 2;
  //   optional group outer_group = 4 {
  //     optional bool in_outer_group = 5;
  //     optional group inner_group = 3 {
  //       optional bool in_inner_group = 1;
  //     }
  //   }
  //   optional bool incomplete
  // }
  @Test public void shouldDetermineCorrectIndexOutsideOfGroups() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(6L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool in_message = 2;
  //   message InnerMessage {
  //     optional bool in_inner_message = 4;
  //     optional bool incomplete
  //   }
  // }
  @Test public void shouldDetermineCorrectIndexInsideOfNestedMessage() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(5L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool in_message = 2;
  //   message InnerMessage {
  //     optional bool in_inner_message = 4;
  //   }
  //   optional bool incomplete
  // }
  @Test public void shouldDetermineCorrectIndexOutsideOfNestedMessage() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(3L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool foo = 1;
  //   reserved 3;
  //   optional bool incomplete
  // }
  @Test public void shouldDetermineCorrectIndexWithSingleReserved() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(4L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool foo = 1;
  //   reserved 3, 5 to 7;
  //   optional bool incomplete
  // }
  @Test public void shouldDetermineCorrectIndexWithReservedRange() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    assertThat(handler.determineNewIndex(incomplete), is(8L));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool incomplete
  // }
  @Test public void shouldComplete() {
    String incompleteFieldName = "incomplete";

    MessageField incomplete = xtext.find(incompleteFieldName, MessageField.class);
    ICompositeNode node = NodeModelUtils.getNode(incomplete);
    ReplaceEdit indexEdit = handler.completeWithIndex(node, 1);

    assertThat(indexEdit.getOffset(),
        is(xtext.text().indexOf(incompleteFieldName) + incompleteFieldName.length()));
    assertThat(indexEdit.getText(), is(" = 1;"));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool incomplete
  //     =
  // }
  @Test public void shouldCompleteAfterExistingEquals() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    ICompositeNode node = NodeModelUtils.getNode(incomplete);
    ReplaceEdit indexEdit = handler.completeWithIndex(node, 1);

    String equalsAtStartOfLine = "    =";
    assertThat(indexEdit.getOffset(),
        is(xtext.text().indexOf(equalsAtStartOfLine) + equalsAtStartOfLine.length()));
    assertThat(indexEdit.getText(), is(" 1;"));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional bool incomplete [ default = true; ];
  // }
  @Test public void shouldCompleteWithoutSemicolonBeforeOptionBracket() {
    String incompleteFieldName = "incomplete";

    MessageField incomplete = xtext.find(incompleteFieldName, MessageField.class);
    ICompositeNode node = NodeModelUtils.getNode(incomplete);
    ReplaceEdit indexEdit = handler.completeWithIndex(node, 1);

    assertThat(indexEdit.getOffset(),
        is(xtext.text().indexOf(incompleteFieldName) + incompleteFieldName.length()));
    assertThat(indexEdit.getText(), is(" = 1 "));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // message Message {
  //   optional group incomplete {
  //   }
  // }
  @Test public void shouldCompleteWithoutSemicolonBeforeGroupBrace() {
    String incompleteGroupName = "incomplete";

    Group incomplete = xtext.find(incompleteGroupName, Group.class);
    ICompositeNode node = NodeModelUtils.getNode(incomplete);
    ReplaceEdit indexEdit = handler.completeWithIndex(node, 1);

    assertThat(indexEdit.getOffset(),
        is(xtext.text().indexOf(incompleteGroupName) + incompleteGroupName.length()));
    assertThat(indexEdit.getText(), is(" = 1 "));
  }

  @Test public void shouldDeleteTrailingWhitespace() {
    String trailingWhitespace = "    ";
    String lineContent = "  optional bool foo" + trailingWhitespace;
    int lineNumber = 10;
    int lineStartOffset = 100;
    int insertionOffset = lineStartOffset + lineContent.lastIndexOf(trailingWhitespace);

    StyledTextContent content = Mockito.mock(StyledTextContent.class);
    Mockito.when(content.getLineAtOffset(insertionOffset)).thenReturn(lineNumber);
    Mockito.when(content.getOffsetAtLine(lineNumber)).thenReturn(lineStartOffset);
    Mockito.when(content.getLine(lineNumber)).thenReturn(lineContent);

    TextEdit trailingWhitespaceEdit = handler.deleteTrailingWhitespace(content, insertionOffset);
    assertThat(trailingWhitespaceEdit.getOffset(), is(insertionOffset));
    assertThat(trailingWhitespaceEdit.getLength(), is(trailingWhitespace.length()));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // // Next Id: 2
  // message Message {
  //   optional bool field = 1;
  //   optional bool incomplete
  // }
  @Test public void shouldUpdateNextIndexComment() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    ReplaceEdit commentEdit = handler.updateNextIndexComment(incomplete, 3);

    String pattern = "Next Id: ";
    assertThat(commentEdit.getOffset(), is(xtext.text().indexOf(pattern) + pattern.length()));
    assertThat(commentEdit.getText(), is("3"));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // /*
  //  * Next Id: 2
  //  */
  // message Message {
  //   optional bool field = 1;
  //   optional bool incomplete
  // }
  @Test public void shouldUpdateMultilineComment() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    ReplaceEdit commentEdit = handler.updateNextIndexComment(incomplete, 3);

    String pattern = "Next Id: ";
    assertThat(commentEdit.getOffset(), is(xtext.text().indexOf(pattern) + pattern.length()));
    assertThat(commentEdit.getText(), is("3"));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // enum Enum {
  //   ONE = 1;
  //   TWO = 2;
  //   THREE = 3;
  //   FOUR
  //   // Next Id: 4
  // }
  @Test public void shouldUpdateNextIndexCommentAtEndOfEnum() {
    Literal incomplete = xtext.find("FOUR", Literal.class);
    ReplaceEdit commentEdit = handler.updateNextIndexComment(incomplete, 5);

    String pattern = "Next Id: ";
    assertThat(commentEdit.getOffset(), is(xtext.text().indexOf(pattern) + pattern.length()));
    assertThat(commentEdit.getText(), is("5"));
  }

  // // ignore errors
  // syntax = "proto2";
  //
  // // My Favorite Number: 200
  // message Message {
  //   optional bool field = 1;
  //   optional bool incomplete
  // }
  @Test public void shouldNotUpdateOtherComment() {
    MessageField incomplete = xtext.find("incomplete", MessageField.class);
    ReplaceEdit commentEdit = handler.updateNextIndexComment(incomplete, 3);

    assertThat(commentEdit, is((ReplaceEdit) null));
  }
}

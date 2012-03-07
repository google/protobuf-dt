/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import static com.google.eclipse.protobuf.formatting.CommentReaderRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.xtext.formatting.*;
import org.eclipse.xtext.formatting.INodeModelFormatter.IFormattedRegion;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.junit.*;

import com.google.inject.Inject;

/**
 * @Tests for <code>{@link ProtobufFormatter}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufFormatter_Test {
  @Rule public CommentReaderRule commentReader = overrideRuntimeModuleWith(unitTestModule());

  @Inject private INodeModelFormatter formatter;

  // syntax = 'proto2';import 'google/protobuf/descriptor.proto';

  // syntax = 'proto2';
  //
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_syntax() {
    assertThatFormattingWorksCorrectly();
  }

  // package com.google.proto.test;import 'google/protobuf/descriptor.proto';

  // package com.google.proto.test;
  //
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_package() {
    assertThatFormattingWorksCorrectly();
  }

  // import 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_normal_import() {
    assertThatFormattingWorksCorrectly();
  }

  // import public 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import public 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_public_import() {
    assertThatFormattingWorksCorrectly();
  }

  // import weak 'dummy.proto';import 'google/protobuf/descriptor.proto';

  // import weak 'dummy.proto';
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_format_weak_import() {
    assertThatFormattingWorksCorrectly();
  }

  // option java_package = "com.foo.bar";option optimize_for = CODE_SIZE;

  // option java_package = "com.foo.bar";
  // option optimize_for = CODE_SIZE;
  @Test public void should_format_native_option() {
    assertThatFormattingWorksCorrectly();
  }

  private void assertThatFormattingWorksCorrectly() {
    ICompositeNode rootNode = commentReader.rootNode();
    IFormattedRegion region = formatter.format(rootNode, 0, rootNode.getText().length());
    String formatted = region.getFormattedText();
    assertThat(formatted, equalTo(commentReader.expectedText()));
  }
}

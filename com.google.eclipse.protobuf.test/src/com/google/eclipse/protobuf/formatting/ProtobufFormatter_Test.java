/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import static org.junit.Assert.assertEquals;

import static com.google.eclipse.protobuf.formatting.CommentReaderRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;

import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.INodeModelFormatter;
import org.eclipse.xtext.formatting.INodeModelFormatter.IFormattedRegion;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.inject.Inject;

/**
 * @Tests for <code>{@link ProtobufFormatter}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufFormatter_Test {
  @Rule public CommentReaderRule commentReader = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

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

  // message Person { optional string name = 1; optional bool active = 2
  // [default = true]; }

  // message Person {
  //   optional string name = 1;
  //   optional bool active = 2 [default = true];
  // }
  @Test public void should_format_message_fields() {
    assertThatFormattingWorksCorrectly();
  }

  // message Person { optional group address = 1 { optional int32
  // number = 2; optional string street = 3; } }

  // message Person {
  //   optional group address = 1 {
  //     optional int32 number = 2;
  //     optional string street = 3;
  //   }
  // }
  @Test public void should_format_groups() {
    assertThatFormattingWorksCorrectly();
  }

  // message Person {}
  // service PersonService {}

  // message Person {
  // }
  //
  // service PersonService {
  // }
  @Test public void should_format_service() {
    assertThatFormattingWorksCorrectly();
  }

  // message Person {}
  // service PersonService { rpc PersonRpc ( Person ) returns ( Person ); }

  // message Person {
  // }
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Person);
  // }
  @Test public void should_format_rpc() {
    assertThatFormattingWorksCorrectly();
  }

  // message Person {}
  //
  // service PersonService { stream PersonStream ( Person,Person ); }

  // message Person {
  // }
  //
  // service PersonService {
  //   stream PersonStream (Person, Person);
  // }
  @Test public void should_format_stream() {
    assertThatFormattingWorksCorrectly();
  }

  // message TestMessage { extensions 1 to 10; }

  // message TestMessage {
  //   extensions 1 to 10;
  // }
  @Test public void should_format_extensions() {
    assertThatFormattingWorksCorrectly();
  }

  // syntax = 'proto2';package com.google.protobuf.test;import 'google/protobuf/descriptor.proto';import
  // public 'address.proto';import weak 'salary.proto';option java_package = "com.foo.bar";option
  // optimize_for = CODE_SIZE;

  // syntax = 'proto2';
  //
  // package com.google.protobuf.test;
  //
  // import 'google/protobuf/descriptor.proto';
  // import public 'address.proto';
  // import weak 'salary.proto';
  //
  // option java_package = "com.foo.bar";
  // option optimize_for = CODE_SIZE;
  @Ignore public void should_format() {
    assertThatFormattingWorksCorrectly();
  }

  private void assertThatFormattingWorksCorrectly() {
    ICompositeNode rootNode = commentReader.rootNode();
    IFormattedRegion region = formatter.format(rootNode, 0, rootNode.getText().length());
    String formatted = region.getFormattedText();
    assertEquals(commentReader.expectedText(), formatted);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      binder().bind(IIndentationInformation.class).toInstance(new IndentationInformationStub());
    }
  }

  private static class IndentationInformationStub implements IIndentationInformation {
    @Override public String getIndentString() {
      return "  ";
    }
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;

import org.eclipse.jface.viewers.StyledString;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.Extensions;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Service;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Labels#labelFor(Object)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Labels_labelFor_Test {
  @Rule public XtextRule xtext = createWith(ProtobufEditorPlugIn.injector());

  @Inject private Labels labels;

  // syntax = "proto2";
  //
  // option optimize_for = SPEED;
  @Test public void should_return_label_for_native_option() {
    NativeOption option = xtext.find("optimize_for", NativeOption.class);
    Object label = labels.labelFor(option);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("optimize_for"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   optional double code = 1;
  //   optional string name = 2;
  //   extensions 10 to max;
  // }
  //
  // extend Type {
  //   optional bool active = 10;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //
  // option (type).(active) = true;
  @Test public void should_return_label_for_custom_option() {
    CustomOption option = xtext.find("type", ")", CustomOption.class);
    Object label = labels.labelFor(option);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("(type).(active)"));
  }

  // syntax = "proto2";
  //
  // message Type {
  //   extensions 1 to 10, 20 to 30, 100 to max;
  // }
  @Test public void should_return_label_for_extensions() {
    Extensions extensions = xtext.findFirst(Extensions.class);
    Object label = labels.labelFor(extensions);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("1 > 10, 20 > 30, 100 > max"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  @Test public void should_return_label_for_import() {
    Import anImport = xtext.findFirst(Import.class);
    Object label = labels.labelFor(anImport);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("google/protobuf/descriptor.proto"));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   HOME = 0;
  // }
  @Test public void should_return_label_for_literal() {
    Literal literal = xtext.find("HOME", Literal.class);
    Object label = labels.labelFor(literal);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("HOME [0]"));
   }

  // syntax = "proto2";
  //
  // message Type {}
  //
  // message Person {
  //   optional Type type = 1;
  // }
  @Test public void should_return_label_for_field() {
    MessageField field = xtext.find("type", MessageField.class);
    Object label = labels.labelFor(field);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("type [1] : Type"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional Type type = 1;
  // }
  @Test public void should_return_label_for_field_with_unresolved_type() {
    MessageField field = xtext.find("type", MessageField.class);
    Object label = labels.labelFor(field);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("type [1] : <unresolved>"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // message Type {}
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Type);
  // }
  @Test public void should_return_label_for_rpc() {
    Rpc rpc = xtext.findFirst(Rpc.class);
    Object label = labels.labelFor(rpc);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonRpc : Person > Type"));
  }

  // syntax = "proto2";
  //
  // message Type {}
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Type);
  // }
  @Test public void should_return_label_for_rpc_with_unresolved_argument_type() {
    Rpc rpc = xtext.findFirst(Rpc.class);
    Object label = labels.labelFor(rpc);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonRpc : <unresolved> > Type"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Type);
  // }
  @Test public void should_return_label_for_rpc_with_unresolved_return_type() {
    Rpc rpc = xtext.findFirst(Rpc.class);
    Object label = labels.labelFor(rpc);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonRpc : Person > <unresolved>"));
  }

  // syntax = "proto2";
  //
  // service PersonService {}
  @Test public void should_return_label_for_service() {
    Service service = xtext.findFirst(Service.class);
    Object label = labels.labelFor(service);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("PersonService"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // message Type {}
  //
  // service PersonService {
  //   stream PersonStream (Person, Type);
  // }
  @Test public void should_return_label_for_stream() {
    Stream stream = xtext.findFirst(Stream.class);
    Object label = labels.labelFor(stream);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonStream (Person, Type)"));
  }

  // syntax = "proto2";
  //
  // message Type {}
  //
  // service PersonService {
  //   stream PersonStream (Person, Type);
  // }
  @Test public void should_return_label_for_stream_with_unresolved_client_message() {
    Stream stream = xtext.findFirst(Stream.class);
    Object label = labels.labelFor(stream);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonStream (<unresolved>, Type)"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // service PersonService {
  //   stream PersonStream (Person, Type);
  // }
  @Test public void should_return_label_for_stream_with_unresolved_server_message() {
    Stream stream = xtext.findFirst(Stream.class);
    Object label = labels.labelFor(stream);
    assertThat(label, instanceOf(StyledString.class));
    StyledString labelText = (StyledString) label;
    assertThat(labelText.getString(), equalTo("PersonStream (Person, <unresolved>)"));
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // extend Person {}
  @Test public void should_return_label_for_type_extension() {
    TypeExtension typeExtension = xtext.findFirst(TypeExtension.class);
    Object label = labels.labelFor(typeExtension);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("Person"));
  }

  // syntax = "proto2";
  //
  // extend Person {}
  @Test public void should_return_label_for_type_extension_with_unresolved_type() {
    TypeExtension typeExtension = xtext.findFirst(TypeExtension.class);
    Object label = labels.labelFor(typeExtension);
    assertThat(label, instanceOf(String.class));
    String labelText = (String) label;
    assertThat(labelText, equalTo("<unresolved>"));
  }
}

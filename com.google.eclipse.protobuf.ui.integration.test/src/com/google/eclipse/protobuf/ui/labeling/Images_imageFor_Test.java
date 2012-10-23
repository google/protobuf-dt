/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION;
import static com.google.eclipse.protobuf.ui.labeling.ProjectFileExists.existsInProject;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Extensions;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.NormalImport;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.PublicImport;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Service;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.eclipse.protobuf.protobuf.WeakImport;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Images#imageFor(Object)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Images_imageFor_Test {
  @Rule public XtextRule xtext = createWith(ProtobufEditorPlugIn.injector());

  @Inject private Images images;

  // syntax = "proto2";
  //
  // enum PhoneType {}
  @Test public void should_return_image_for_enum() {
    Enum anEnum = xtext.find("PhoneType", Enum.class);
    String image = images.imageFor(anEnum);
    assertThat(image, equalTo("enum.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // extend Person {}
  @Test public void should_return_image_for_type_extension() {
    TypeExtension typeExtension = xtext.findFirst(TypeExtension.class);
    String image = images.imageFor(typeExtension);
    assertThat(image, equalTo("typeextension.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {
  //   extensions 1 to 10;
  // }
  @Test public void should_return_image_for_extensions() {
    Extensions extensions = xtext.findFirst(Extensions.class);
    String image = images.imageFor(extensions);
    assertThat(image, equalTo("extensions.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional group Address = 1 {}
  // }
  @Test public void should_return_image_for_group() {
    Group group = xtext.findFirst(Group.class);
    String image = images.imageFor(group);
    assertThat(image, equalTo("group.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  @Test public void should_return_image_for_normal_import() {
    NormalImport anImport = xtext.findFirst(NormalImport.class);
    String image = images.imageFor(anImport);
    assertThat(image, equalTo("import.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // import public "google/protobuf/descriptor.proto";
  @Test public void should_return_image_for_public_import() {
    PublicImport anImport = xtext.findFirst(PublicImport.class);
    String image = images.imageFor(anImport);
    assertThat(image, equalTo("import.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // import weak "google/protobuf/descriptor.proto";
  @Test public void should_return_image_for_weak_import() {
    WeakImport anImport = xtext.findFirst(WeakImport.class);
    String image = images.imageFor(anImport);
    assertThat(image, equalTo("import.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   HOME = 0;
  // }
  @Test public void should_return_image_for_literal() {
    Literal literal = xtext.find("HOME", Literal.class);
    String image = images.imageFor(literal);
    assertThat(image, equalTo("literal.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {}
  @Test public void should_return_image_for_message() {
    Message message = xtext.find("Person", Message.class);
    String image = images.imageFor(message);
    assertThat(image, equalTo("message.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // option optimize_for = SPEED;
  @Test public void should_return_image_for_native_option() {
    NativeOption option = xtext.find("optimize_for", NativeOption.class);
    String image = images.imageFor(option);
    assertThat(image, equalTo("option.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // package com.google.proto;
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FileOptions {
  //   optional int32 code = 1000;
  //   optional int32 info = 1001;
  // }
  //
  // option (code) = 68;
  @Test public void should_return_image_for_custom_option() {
    CustomOption option = xtext.find("code", ")", CustomOption.class);
    String image = images.imageFor(option);
    assertThat(image, equalTo("option.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // package com.google.proto.test;
  @Test public void should_return_image_for_package() {
    Package aPackage = xtext.findFirst(Package.class);
    String image = images.imageFor(aPackage);
    assertThat(image, equalTo("package.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Person);
  // }
  @Test public void should_return_image_for_rpc() {
    Rpc rpc = xtext.findFirst(Rpc.class);
    String image = images.imageFor(rpc);
    assertThat(image, equalTo("rpc.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {}
  //
  // service PersonService {
  //   rpc PersonRpc (Person) returns (Person);
  // }
  @Test public void should_return_image_for_service() {
    Service service = xtext.findFirst(Service.class);
    String image = images.imageFor(service);
    assertThat(image, equalTo("service.gif"));
    assertThat(image, existsInProject());
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
  @Test public void should_return_image_for_stream() {
    Stream stream = xtext.findFirst(Stream.class);
    String image = images.imageFor(stream);
    assertThat(image, equalTo("stream.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  @Test public void should_return_image_for_syntax() {
    Syntax syntax = xtext.findFirst(Syntax.class);
    String image = images.imageFor(syntax);
    assertThat(image, equalTo("syntax.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional string name = 1;
  // }
  @Test public void should_return_image_for_optional_field() {
    MessageField field = xtext.find("name", MessageField.class);
    String image = images.imageFor(field);
    assertThat(image, equalTo("optional.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {
  //  required string name = 1;
  // }
  @Test public void should_return_image_for_required_field() {
    MessageField field = xtext.find("name", MessageField.class);
    String image = images.imageFor(field);
    assertThat(image, equalTo("required.gif"));
    assertThat(image, existsInProject());
  }

  // syntax = "proto2";
  //
  // message Person {
  //  repeated string name = 1;
  // }
  @Test public void should_return_image_for_repeated_field() {
    MessageField field = xtext.find("name", MessageField.class);
    String image = images.imageFor(field);
    assertThat(image, equalTo("repeated.gif"));
    assertThat(image, existsInProject());
  }

  @Test public void should_return_image_for_imports() {
    String image = images.imageFor("imports");
    assertThat(image, equalTo("imports.gif"));
    assertThat(image, existsInProject());
  }

  @Test public void should_return_image_for_options() {
    String image = images.imageFor("options");
    assertThat(image, equalTo("options.gif"));
    assertThat(image, existsInProject());
  }

  @Test public void should_return_image_for_OPTION_EClass() {
    String image = images.imageFor(OPTION);
    assertThat(image, equalTo("option.gif"));
    assertThat(image, existsInProject());
  }

  @Test public void should_return_image_for_SYNTAX_keyword() {
    String image = images.imageFor(CommonKeyword.SYNTAX);
    assertThat(image, equalTo("syntax.gif"));
    assertThat(image, existsInProject());
  }

  @Test public void should_contain_default_image() {
    String image = images.defaultImage();
    assertThat(image, equalTo("empty.gif"));
    assertThat(image, existsInProject());
  }
}
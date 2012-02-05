/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.junit.*;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufElementMatcher#findUriOfMatchingProtobufElement(IResourceDescription, CppToProtobufMapping)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementMatcher_findUriOfMatchingProtobufElement_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IQualifiedNameConverter fqnConverter;
  @Inject private ResourceSetBasedResourceDescriptions index;
  @Inject private ProtobufElementMatcher matcher;

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Outer {
  //   message Inner {}
  // }
  @Test public void should_return_URI_nested_message_when_qualified_name_does_not_contain_underscores() {
    CppToProtobufMapping mapping = messageMapping("com.google.proto.Outer.Inner");
    URI foundUri = matcher.findUriOfMatchingProtobufElement(descriptionOf(xtext.resource()), mapping);
    assertThat(foundUri, equalTo(uriOfMessageWithName("Inner")));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Outer {
  //   message Inner {}
  // }
  @Test public void should_return_URI_nested_message_when_underscores_in_qualified_name_represent_nesting() {
    CppToProtobufMapping mapping = messageMapping("com.google.proto.Outer_Inner");
    URI foundUri = matcher.findUriOfMatchingProtobufElement(descriptionOf(xtext.resource()), mapping);
    assertThat(foundUri, equalTo(uriOfMessageWithName("Inner")));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Outer_Message {
  //   message Inner {}
  // }
  @Test public void should_return_URI_nested_message_when_underscores_are_part_of_naming() {
    CppToProtobufMapping mapping = messageMapping("com.google.proto.Outer_Message_Inner");
    URI foundUri = matcher.findUriOfMatchingProtobufElement(descriptionOf(xtext.resource()), mapping);
    assertThat(foundUri, equalTo(uriOfMessageWithName("Inner")));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Outer_Message {
  //   message Inner {}
  // }
  //
  // message Outer {
  //   message Message {
  //     message Inner {}
  //   }
  // }
  @Test public void should_return_first_match_if_both_qualified_names_and_types_match() {
    CppToProtobufMapping mapping = messageMapping("com.google.proto.Outer_Message_Inner");
    URI foundUri = matcher.findUriOfMatchingProtobufElement(descriptionOf(xtext.resource()), mapping);
    assertThat(foundUri, equalTo(uriOfMessageWithName("Inner")));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Outer {
  //   message Inner {}
  // }
  @Test public void should_return_null_if_match_not_found() {
    CppToProtobufMapping mapping = messageMapping("com.google.proto.Outer__Inner");
    URI foundUri = matcher.findUriOfMatchingProtobufElement(descriptionOf(xtext.resource()), mapping);
    assertNull(foundUri);
  }

  private CppToProtobufMapping messageMapping(String qualifiedNameAsText) {
    QualifiedName qualifiedName = fqnConverter.toQualifiedName(qualifiedNameAsText);
    return new CppToProtobufMapping(qualifiedName, Message.class);
  }

  private URI uriOfMessageWithName(String name) {
    XtextResource resource = xtext.resource();
    Message message = xtext.find(name, Message.class);
    String fragment = resource.getURIFragment(message);
    return resource.getURI().appendFragment(fragment);
  }

  private IResourceDescription descriptionOf(Resource resource) {
    index.setContext(resource);
    return index.getResourceDescription(resource.getURI());
  }
}

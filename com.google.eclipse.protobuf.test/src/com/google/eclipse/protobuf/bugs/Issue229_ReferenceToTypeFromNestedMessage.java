/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ExtensibleType;
import com.google.eclipse.protobuf.protobuf.TypeExtension;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=229">Issue 229</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue229_ReferenceToTypeFromNestedMessage {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  // syntax = "proto2";
  //
  // message OuterProto {
  //   message ProtoWithExtensions {
  //     required string string_value = 1;
  //     extensions 100 to max;
  //   }
  //
  //   message Extension {
  //     extend ProtoWithExtensions {
  //       optional Extension the_extension = 100;
  //     }
  //     required string string_value = 1;
  //   }
  // }
  @Test public void should_recognize_extendMessage_in_nestedMessage() {
    TypeExtension extension = xtext.findFirst(TypeExtension.class);
    ExtensibleType extended = extension.getType().getTarget();
    assertThat(extended.getName(), equalTo("ProtoWithExtensions"));
  }
}

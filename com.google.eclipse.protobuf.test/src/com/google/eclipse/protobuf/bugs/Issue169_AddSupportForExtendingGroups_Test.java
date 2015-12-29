/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ExtensibleTypeLink;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Message;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=169">Issue 169</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue169_AddSupportForExtendingGroups_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  // // Create file types.proto
  //
  // syntax = 'proto2';
  // package google.proto.test;
  //
  // message TopMessage {
  //   optional group MidGroup = 1 {
  //     optional group BottomGroup = 2 {}
  //   }
  // }

  // syntax = "proto2";
  // package com.google.proto.project.shared;
  //
  // import "types.proto";
  //
  // extend .google.proto.test.TopMessage.MidGroup.BottomGroup {}
  @Test public void should_extend_group_inside_group() {
    ExtensibleTypeLink link = xtext.find("BottomGroup", " {", ExtensibleTypeLink.class);
    Group group = (Group) link.getTarget();
    assertThat(group.getName(), equalTo("BottomGroup"));
  }

  // // Create file types.proto
  //
  // syntax = 'proto2';
  // package google.proto.test;
  //
  // message TopMessage {
  //   message MidMessage {
  //     optional group BottomGroup = 1 {}
  //   }
  // }

  // syntax = "proto2";
  // package com.google.proto.project.shared;
  //
  // import "types.proto";
  //
  // extend .google.proto.test.TopMessage.MidMessage.BottomGroup {}
  @Test public void should_extend_group_inside_message() {
    ExtensibleTypeLink link = xtext.find("BottomGroup", " {", ExtensibleTypeLink.class);
    Group group = (Group) link.getTarget();
    assertThat(group.getName(), equalTo("BottomGroup"));
  }

  // // Create file types.proto
  //
  // syntax = 'proto2';
  // package google.proto.test;
  //
  // message TopMessage {
  //   optional group MidGroup = 1 {
  //     message BottomMessage {}
  //   }
  // }

  // syntax = "proto2";
  // package com.google.proto.project.shared;
  //
  // import "types.proto";
  //
  // extend .google.proto.test.TopMessage.MidGroup.BottomMessage {}
  @Test public void should_extend_message_inside_group() {
    ExtensibleTypeLink link = xtext.find("BottomMessage", " {", ExtensibleTypeLink.class);
    Message message = (Message) link.getTarget();
    assertThat(message.getName(), equalTo("BottomMessage"));
  }
}

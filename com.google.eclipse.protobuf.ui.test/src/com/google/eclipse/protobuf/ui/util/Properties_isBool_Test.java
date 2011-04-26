/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.google.eclipse.protobuf.ui.util;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Properties#isBool(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isBool_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_true_if_property_is_bool() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ");
    proto.append("  optional bool active = 0;");
    proto.append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property activeProperty = allPropertiesInFirstMessage(root).get(0);
    assertThat(properties.isBool(activeProperty), equalTo(true));
  }

  @Test public void should_return_false_if_property_is_not_bool() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ");
    proto.append("  optional string name = 0;");
    proto.append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property nameProperty = allPropertiesInFirstMessage(root).get(0);
    assertThat(properties.isBool(nameProperty), equalTo(false));
  }

  private List<Property> allPropertiesInFirstMessage(Protobuf root) {
    List<Message> messages = getAllContentsOfType(root, Message.class);
    return getAllContentsOfType(messages.get(0), Property.class);
  }
}

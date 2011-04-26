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
 * Tests for <code>{@link Properties#calculateTagNumberOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_calculateTagNumberOf_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_one_for_first_and_only_property() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ");
    proto.append("  required string name = 2;");
    proto.append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property nameProperty = allPropertiesInFirstMessage(root).get(0);
    int index = properties.calculateTagNumberOf(nameProperty);
    assertThat(index, equalTo(1));
  }

  @Test public void should_return_max_tag_number_value_plus_one_for_new_property() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ");
    proto.append("  required string name = 6;");
    proto.append("  required int32 id = 8;   ");
    proto.append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property idProperty = allPropertiesInFirstMessage(root).get(1);
    int index = properties.calculateTagNumberOf(idProperty);
    assertThat(index, equalTo(7));
  }

  private List<Property> allPropertiesInFirstMessage(Protobuf root) {
    List<Message> messages = getAllContentsOfType(root, Message.class);
    return getAllContentsOfType(messages.get(0), Property.class);
  }
}

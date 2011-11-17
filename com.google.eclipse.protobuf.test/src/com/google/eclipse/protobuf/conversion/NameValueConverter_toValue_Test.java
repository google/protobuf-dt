/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Name;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;

/**
 * Tests for <code>{@link NameValueConverter#toValue(String, INode)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NameValueConverter_toValue_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  private INode node;
  private NameValueConverter converter;
  
  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.getInstanceOf(NameValueConverter.class);
  }
  
  @Test public void should_return_name_using_given_value_if_given_value_is_not_null() {
    Name name = converter.toValue("hello", node);
    assertThat(name.getValue(), equalTo("hello"));
  }
  
  @Test public void should_return_name_using_text_from_node_if_value_is_null_and_text_in_node_is_keyword() {
    when(node.getText()).thenReturn("message");
    Name name = converter.toValue(null, node);
    assertThat(name.getValue(), equalTo("message"));
  }
  
  @Test public void should_return_null_if_given_value_is_null_and_text_in_node_is_not_keyword() {
    when(node.getText()).thenReturn("hello");
    assertNull(converter.toValue(null, node));
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.parser;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

import org.antlr.runtime.CharStream;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.ui.ProtobufPlugIn;

/**
 * Tests for <code>{@link PreferenceDrivenProtobufParser#doParse(String, CharStream, NodeModelBuilder, int)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceDrivenProtobufParser_doParse_Test {
  private static String proto1;

  @BeforeClass public static void setUpOnce() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("// ignore errors")
         .append("c++header #include 'test/common/proto_class.h'");
    proto1 = proto.toString();
  }

  @Rule public XtextRule xtext = createWith(ProtobufPlugIn.injector());

  private IPreferenceStore store;

  @Before public void setUp() {
    IPreferenceStoreAccess storeAccess = xtext.getInstanceOf(IPreferenceStoreAccess.class);
    store = storeAccess.getWritablePreferenceStore();
  }

  @Test public void should_recognize_proto1_syntax() {
    store.setValue("parser.checkProto2Only", true);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertThat(root, instanceOf(NonProto2Protobuf.class));
  }

  @Test public void should_not_recognize_proto1_syntax() {
    store.setValue("parser.checkProto2Only", false);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertNull(root);
  }
}

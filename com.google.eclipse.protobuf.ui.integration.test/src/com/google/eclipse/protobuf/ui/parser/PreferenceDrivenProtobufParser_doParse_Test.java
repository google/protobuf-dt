/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.parser;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;

import org.antlr.runtime.CharStream;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Inject;

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

  @Rule public XtextRule xtext = createWith(ProtobufEditorPlugIn.injector());

  @Inject private IPreferenceStoreAccess storeAccess;
  private IPreferenceStore store;

  @Before public void setUp() {
    store = storeAccess.getWritablePreferenceStore();
  }

  @Test public void should_recognize_proto1_syntax() {
    store.setValue("misc.googleInternal", true);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertThat(root, instanceOf(NonProto2Protobuf.class));
  }

  @Test public void should_not_recognize_proto1_syntax() {
    store.setValue("misc.googleInternal", false);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertNull(root);
  }
}

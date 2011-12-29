/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.parser;

import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.antlr.runtime.CharStream;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.*;

/**
 * Tests for <code>{@link Proto2OnlyParser#doParse(String, CharStream, NodeModelBuilder, int)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Proto2OnlyParser_doParse_Test {

  private static String proto1;

  @BeforeClass public static void setUpOnce() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("// ignore errors")
         .append("c++header #include 'test/common/proto_class.h'");
    proto1 = proto.toString();
  }

  @Rule public XtextRule xtext = createWith(new Setup());

  private ParserChecksSettingsProvider settingsProvider;

  @Before public void setUp() {
    settingsProvider = xtext.getInstanceOf(ParserChecksSettingsProvider.class);
  }

  @Test public void should_recognize_proto1_syntax() {
    when(settingsProvider.shouldCheckProto2Only()).thenReturn(true);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertThat(root, instanceOf(NonProto2Protobuf.class));
  }

  @Test public void should_not_recognize_proto1_syntax() {
    when(settingsProvider.shouldCheckProto2Only()).thenReturn(false);
    xtext.parseText(proto1);
    Protobuf root = xtext.root();
    assertNull(root);
  }

  private static class Setup extends UnitTestSetup {
    @Override public Injector createInjector() {
      return Guice.createInjector(new Module() {
        @SuppressWarnings("unused")
        public void configureParserChecksSettingsProvider(Binder binder) {
          binder.bind(ParserChecksSettingsProvider.class).toInstance(mock(ParserChecksSettingsProvider.class));
        }
      });
    }
  }

}

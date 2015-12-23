/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.cdt.matching.Resources.eObjects;
import static com.google.eclipse.protobuf.cdt.util.ExtendedListIterator.newIterator;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.cdt.util.ExtendedIterator;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link MessageMatcherStrategy#matchingProtobufElementLocations(EObject, ExtendedIterator)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MessageMatcherStrategy_matchingProtobufElementLocations_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private MessageMatcherStrategy matcher;

  // syntax = "proto2";
  //
  // message Person {}
  //
  // message Address {}
  @Test public void should_find_top_level_perfect_match() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Address"));
    EObject message = xtext.find("Address", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   message Address {}
  // }
  @Test public void should_find_nested_perfect_match() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person", "Address"));
    EObject message = xtext.find("Address", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   message Address {
  //     message Type {}
  //   }
  // }
  @Test public void should_find_nested_match_1() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person_Address_Type"));
    EObject message = xtext.find("Type", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person_Address {
  //   message Type {}
  // }
  @Test public void should_find_nested_match_2() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person_Address_Type"));
    EObject message = xtext.find("Type", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   message Address_Type {}
  // }
  @Test public void should_find_nested_match_3() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person_Address_Type"));
    EObject message = xtext.find("Address_Type", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   message Address {
  //     message Name {}
  //     message Type {}
  //   }
  // }
  @Test public void should_find_nested_match_4() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person_Address", "Type"));
    EObject message = xtext.find("Type", Message.class);
    assertThat(eObjectsFrom(locations), hasItems(message));
  }

  // syntax = "proto2";
  //
  // message Person_Address {
  //   message Type{}
  // }
  //
  // message Person {
  //   message Address {
  //     message Type {}
  //   }
  // }
  @Test public void should_find_nested_match_5() {
    List<URI> locations = matcher.matchingProtobufElementLocations(xtext.root(), newIterator("Person_Address", "Type"));
    EObject[] messages = { xtext.find("Type{}", 4, Message.class), xtext.find("Type {}", 4, Message.class) };
    assertThat(eObjectsFrom(locations), hasItems(messages));
  }

  private List<EObject> eObjectsFrom(List<URI> locations) {
    return eObjects(xtext.resource(), locations);
  }
}

/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ImportValidator#checkUnknownSyntaxImports(Protobuf)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator_checkUnknownSyntaxImports_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private ImportValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // // Create file C.proto
  //
  // syntax = 'proto2';

  // // Create file B.proto
  //
  // syntax = 'proto2';
  //
  // import "C.proto";

  // syntax = "proto2";
  //
  // import "B.proto";
  // import "C.proto";
  @Test public void should_not_add_warnings_if_imported_files_are_proto2() {
    validator.checkUnknownSyntaxImports(xtext.root());
    verifyNoMoreInteractions(messageAcceptor);
  }

  // // Create file C.proto
  //
  // syntax = 'proto2';
  //
  // import "B.proto";

  // // Create file B.proto
  //
  // syntax = 'proto2';
  //
  // import "C.proto";

  // syntax = "proto2";
  //
  // import "B.proto";
  // import "C.proto";
  @Test public void should_not_add_warnings_if_imported_files_are_proto2_even_with_circular_dependencies() {
    validator.checkUnknownSyntaxImports(xtext.root());
    verifyNoMoreInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // import "093651b0-5676-11e1-b86c-0800200c9a66.proto";
  @Test public void should_not_add_warnings_if_imported_file_does_not_exist() {
    validator.checkUnknownSyntaxImports(xtext.root());
    verifyNoMoreInteractions(messageAcceptor);
  }
}

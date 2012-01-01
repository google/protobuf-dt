/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE__NAME;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MORE_THAN_ONE_PACKAGE_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.*;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkOnlyOnePackageDefinition(Package)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkOnlyOnePackageDefinition_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = xtext.getInstanceOf(ProtobufJavaValidator.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // package com.google.protobuf;
  // package com.google.eclipse;
  @Test public void should_create_error_if_there_are_more_than_one_package_definitions() {
    Package p = xtext.find("com.google.eclipse", Package.class);
    validator.checkOnlyOnePackageDefinition(p);
    String message = "Multiple package definitions.";
    verify(messageAcceptor).acceptError(message, p, PACKAGE__NAME, INSIGNIFICANT_INDEX, MORE_THAN_ONE_PACKAGE_ERROR);
  }

  // syntax = "proto2";
  //
  // package com.google.eclipse;
  @Test public void should_not_create_error_if_there_is_only_one_package_definition() {
    Package p = xtext.find("com.google.eclipse", Package.class);
    validator.checkOnlyOnePackageDefinition(p);
    verifyZeroInteractions(messageAcceptor);
  }
}

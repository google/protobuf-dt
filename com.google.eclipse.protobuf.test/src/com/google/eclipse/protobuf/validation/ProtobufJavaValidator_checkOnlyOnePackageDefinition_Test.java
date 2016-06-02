/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE__NAME;
import static com.google.eclipse.protobuf.validation.Messages.multiplePackages;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MORE_THAN_ONE_PACKAGE_ERROR;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkOnlyOnePackageDefinition(Package)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkOnlyOnePackageDefinition_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // package com.google.protobuf;
  // package com.google.eclipse;
  @Test public void should_create_error_if_there_are_more_than_one_package_definitions() {
    Package p = xtext.find("com.google.eclipse", Package.class);
    validator.checkOnlyOnePackageDefinition(p);
    verify(messageAcceptor).acceptError(multiplePackages, p, PACKAGE__NAME, INSIGNIFICANT_INDEX, MORE_THAN_ONE_PACKAGE_ERROR);
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

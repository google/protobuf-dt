/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PackageFinder.findPackage;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE__NAME;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MORE_THAN_ONE_PACKAGE_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkOnlyOnePackageDefinition(Package)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkOnlyOnePackageDefinition_Test {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();
  
  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;
  
  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = xtext.getInstanceOf(ProtobufJavaValidator.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  @Test public void should_create_error_if_there_are_more_than_one_package_definitions() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package com.google.protobuf;")
         .append("package com.google.eclipse; ");
    Protobuf root = xtext.parseText(proto);
    Package p = findPackage(name("com.google.eclipse"), in(root));
    validator.checkOnlyOnePackageDefinition(p);
    String message = "Multiple package definitions.";
    verify(messageAcceptor).acceptError(message, p, PACKAGE__NAME, INSIGNIFICANT_INDEX, MORE_THAN_ONE_PACKAGE_ERROR);
  }

  @Test public void should_not_create_error_if_there_is_only_one_package_definition() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("package com.google.eclipse; ");
    Protobuf root = xtext.parseText(proto);
    Package p = findPackage(name("com.google.eclipse"), in(root));
    validator.checkOnlyOnePackageDefinition(p);
    verifyZeroInteractions(messageAcceptor);
  }
}

/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.IndexRange;
import com.google.eclipse.protobuf.protobuf.ProtobufPackage;
import com.google.inject.Inject;

public class ProtobufJavaValidator_checkIndexRangeBounds {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved -2 to 2;
  // }
  @Test public void should_error_on_negative_bounds() {
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    validator.checkIndexRangeBounds(indexRanges.get(0));
    verifyError(
        "Extensions and reserved numbers must be positive.",
        indexRanges.get(0),
        ProtobufPackage.Literals.INDEX_RANGE__FROM);
    verifyNoMoreInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved 3 to 1;
  //   reserved 4 to 4;
  // }
  @Test public void should_error_on_end_less_than_start() {
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    validator.checkIndexRangeBounds(indexRanges.get(0));
    validator.checkIndexRangeBounds(indexRanges.get(1));
    verifyError("End number must be greater than or equal to start number.", indexRanges.get(0));
    verifyNoMoreInteractions(messageAcceptor);
  }

  private void verifyError(String message, EObject errorSource) {
    verifyError(message, errorSource, null);
  }

  private void verifyError(String message, EObject errorSource, EStructuralFeature errorFeature) {
    verify(messageAcceptor).acceptError(message, errorSource, errorFeature, -1, null);
  }
}

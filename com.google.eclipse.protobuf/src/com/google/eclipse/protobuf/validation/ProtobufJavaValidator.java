/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.validation.Messages.*;

import org.eclipse.xtext.validation.Check;

import com.google.eclipse.protobuf.protobuf.Property;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator extends AbstractProtobufJavaValidator {

  /**
   * Creates an error if the tag number of the given property is less than zero.
   * @param property the given property.
   */
  @Check public void checkTagNumberIsGreaterThanZero(Property property) {
    if (property.getName() == null) return; // we already show an error if name is null, no need to go further.
    int index = property.getIndex();
    if (index > 0) return;
    String msg = (index == 0) ? Error_fieldNumbersMustBePositive : Error_expectedFieldNumber;
    error(msg, property.eClass().getEStructuralFeature("index"));
  }

}

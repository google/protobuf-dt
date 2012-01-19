/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.ABSTRACT_OPTION__VALUE;
import static com.google.eclipse.protobuf.validation.Messages.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.validation.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DataTypeValidator extends AbstractDeclarativeValidator {
  public static final String EXPECTED_BOOL_ERROR = "expectedBool";
  public static final String EXPECTED_STRING_ERROR = "expectedString";

  @Override public void register(EValidatorRegistrar registrar) {}

  @Inject private IQualifiedNameProvider fqnProvider;
  @Inject private MessageFields messageFields;
  @Inject private INodes nodes;

  @Check public void checkValueOfDefaultTypeMatchesFieldType(DefaultValueFieldOption option) {
    EObject container = option.eContainer();
    if (!(container instanceof MessageField)) {
      return;
    }
    MessageField field = (MessageField) container;
    if (messageFields.isBool(field)) {
      validateBool(option);
      return;
    }
    if (messageFields.isFloatingPointNumber(field)) {
      validateFloatingPointNumber(option);
      return;
    }
    if (messageFields.isInteger(field)) {
      validateInteger(option);
      return;
    }
    if (messageFields.isUnsignedInteger(field)) {
      validateUnsignedInteger(option);
      return;
    }
    if (messageFields.isBytes(field) || messageFields.isString(field)) {
      validateString(option);
      return;
    }
    Enum anEnum = messageFields.enumTypeOf(field);
    if (anEnum != null) {
      validateEnumLiteral(option, anEnum);
    }
  }

  private void validateBool(DefaultValueFieldOption option) {
    Value value = option.getValue();
    if (!(value instanceof BooleanLink)) {
      error(expectedTrueOrFalse, option, ABSTRACT_OPTION__VALUE, EXPECTED_BOOL_ERROR);
    }
  }

  private void validateFloatingPointNumber(DefaultValueFieldOption option) {
    Value value = option.getValue();
    if (!(value instanceof DoubleLink) && !isInteger(value)) {
      error(expectedNumber, ABSTRACT_OPTION__VALUE);
    }
  }

  private void validateInteger(DefaultValueFieldOption option) {
    Value value = option.getValue();
    if (!isInteger(value)) {
      error(expectedInteger, ABSTRACT_OPTION__VALUE);
    }
  }

  private boolean isInteger(Value value) {
    return value instanceof LongLink || value instanceof HexNumberLink;
  }

  private void validateUnsignedInteger(DefaultValueFieldOption option) {
    Value value = option.getValue();
    long longValue = longValueIn(value);
    if (longValue < 0) {
      error(expectedPositiveNumber, ABSTRACT_OPTION__VALUE);
    }
  }

  private long longValueIn(Value value) {
    if (value instanceof LongLink) {
      LongLink link = (LongLink) value;
      return link.getTarget();
    }
    if (value instanceof HexNumberLink) {
      HexNumberLink link = (HexNumberLink) value;
      return link.getTarget();
    }
    throw new IllegalArgumentException(value + " does not belong to an integer type");
  }

  private void validateString(DefaultValueFieldOption option) {
    Value value = option.getValue();
    if (!(value instanceof StringLink)) {
      error(expectedString, option, ABSTRACT_OPTION__VALUE, EXPECTED_STRING_ERROR);
    }
  }

  private boolean validateEnumLiteral(DefaultValueFieldOption option, Enum anEnum) {
    Value value = option.getValue();
    if (anEnum == null) {
      return false;
    }
    if (!(value instanceof LiteralLink)) {
      error(expectedIdentifier, ABSTRACT_OPTION__VALUE);
      return true;
    }
    Literal literal = ((LiteralLink) value).getTarget();
    if (!anEnum.equals(literal.eContainer())) {
      QualifiedName enumFqn = fqnProvider.getFullyQualifiedName(anEnum);
      String literalName = nodes.textOf(nodeForValueFeatureIn(option));
      String msg = String.format(literalNotInEnum, enumFqn, literalName);
      error(msg, ABSTRACT_OPTION__VALUE);
    }
    return true;
  }

  private INode nodeForValueFeatureIn(DefaultValueFieldOption option) {
    return nodes.firstNodeForFeature(option, ABSTRACT_OPTION__VALUE);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.FIELD_OPTION__VALUE;
import static com.google.eclipse.protobuf.validation.Messages.*;

import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.validation.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DataTypeValidator extends AbstractDeclarativeValidator {

  public static final String EXPECTED_BOOL_ERROR = "expectedBool";
  public static final String EXPECTED_STRING_ERROR = "expectedString";

  @Override public void register(EValidatorRegistrar registrar) {}

  @Inject private FieldOptions fieldOptions;
  @Inject private IQualifiedNameProvider fqnProvider;
  @Inject private MessageFields messageFields;
  @Inject private ModelFinder modelFinder;
  @Inject private INodes nodes;

  @Check public void checkValueOfDefaultTypeMatchesFieldType(FieldOption option) {
    if (fieldOptions.isDefaultValueOption(option)) {
      MessageField field = (MessageField) option.eContainer();
      checkValueTypeMatchesFieldType(option, field);
    }
  }

  private void checkValueTypeMatchesFieldType(FieldOption option, MessageField field) {
    if (validateBool(option, field)) {
      return;
    }
    if (validateFloatingPointNumber(option, field)) {
      return;
    }
    if (validateInteger(option, field)) {
      return;
    }
    if (validateString(option, field)) {
      return;
    }
    validateEnumLiteral(option, field);
  }

  private boolean validateBool(FieldOption option, MessageField field) {
    if (!messageFields.isBool(field)) {
      return false;
    }
    Value value = option.getValue();
    if (!(value instanceof BooleanLink)) {
      error(expectedTrueOrFalse, option, FIELD_OPTION__VALUE, EXPECTED_BOOL_ERROR);
    }
    return true;
  }

  private boolean validateFloatingPointNumber(FieldOption option, MessageField field) {
    if (!messageFields.isFloatingPointNumber(field)) {
      return false;
    }
    Value value = option.getValue();
    if (!(value instanceof DoubleLink) && !isInteger(value)) {
      error(expectedNumber, FIELD_OPTION__VALUE);
    }
    return true;
  }

  private boolean validateInteger(FieldOption option, MessageField field) {
    if (!messageFields.isInteger(field)) {
      return false;
    }
    Value value = option.getValue();
    if (!isInteger(value)) {
      error(expectedInteger, FIELD_OPTION__VALUE);
      return true;
    }
    if (messageFields.isUnsignedInteger(field)) {
      long longValue = longValueIn(value);
      if (longValue < 0) {
        error(expectedPositiveNumber, FIELD_OPTION__VALUE);
      }
    }
    return true;
  }

  private boolean isInteger(Value value) {
    return value instanceof LongLink || value instanceof HexNumberLink;
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

  private boolean validateString(FieldOption option, MessageField field) {
    if (!messageFields.isBytes(field) && !messageFields.isString(field)) {
      return false;
    }
    Value value = option.getValue();
    if (!(value instanceof StringLink)) {
      error(expectedString, option, FIELD_OPTION__VALUE, EXPECTED_STRING_ERROR);
    }
    return true;
  }

  private boolean validateEnumLiteral(FieldOption option, MessageField field) {
    Value value = option.getValue();
    Enum anEnum = modelFinder.enumTypeOf(field);
    if (anEnum == null) {
      return false;
    }
    if (!(value instanceof LiteralLink)) {
      error(expectedIdentifier, FIELD_OPTION__VALUE);
      return true;
    }
    Literal literal = ((LiteralLink) value).getTarget();
    if (!anEnum.equals(literal.eContainer())) {
      QualifiedName enumFqn = fqnProvider.getFullyQualifiedName(anEnum);
      String literalName = nodes.textOf(nodeForValueFeatureIn(option));
      String msg = String.format(literalNotInEnum, enumFqn, literalName);
      error(msg, FIELD_OPTION__VALUE);
    }
    return true;
  }

  private INode nodeForValueFeatureIn(FieldOption option) {
    return nodes.firstNodeForFeature(option, FIELD_OPTION__VALUE);
  }
}

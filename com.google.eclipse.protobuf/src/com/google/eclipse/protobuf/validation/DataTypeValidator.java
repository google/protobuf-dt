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

  @Override public void register(EValidatorRegistrar registrar) {}

  @Inject private FieldOptions fieldOptions;
  @Inject private IQualifiedNameProvider fqnProvider;
  @Inject private MessageFields messageFields;
  @Inject private ModelFinder modelFinder;
  @Inject private INodes nodes;

  @Check public void checkValueOfDefaultTypeMatchesFieldType(FieldOption option) {
    if (fieldOptions.isDefaultValueOption(option)) {
      MessageField field = (MessageField) option.eContainer();
      INode nodeForValueFeature = nodes.firstNodeForFeature(option, FIELD_OPTION__VALUE);
      checkValueTypeMatchesFieldType(option.getValue(), nodeForValueFeature, field);
    }
  }

  private  void checkValueTypeMatchesFieldType(Value value, INode nodeForValueFeature, MessageField field) {
    if (messageFields.isString(field)) {
      if (!(value instanceof StringLink)) {
        error(expectedString, FIELD_OPTION__VALUE);
      }
      return;
    }
    if (messageFields.isBool(field)) {
      if (!(value instanceof BooleanLink)) {
        error(expectedTrueOrFalse, FIELD_OPTION__VALUE);
      }
      return;
    }
    Enum anEnum = modelFinder.enumTypeOf(field);
    if (anEnum != null) {
      if (!(value instanceof LiteralLink)) {
        error(expectedIdentifier, FIELD_OPTION__VALUE);
      }
      Literal literal = ((LiteralLink) value).getTarget();
      if (!anEnum.equals(literal.eContainer())) {
        QualifiedName enumFqn = fqnProvider.getFullyQualifiedName(anEnum);
        String literalName = nodes.textOf(nodeForValueFeature);
        String msg = String.format(literalNotInEnum, enumFqn, literalName);
        error(msg, FIELD_OPTION__VALUE);
      }
      return;
    }
  }
}

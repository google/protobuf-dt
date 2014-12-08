/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MAP_TYPE__KEY_TYPE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MAP_TYPE__VALUE_TYPE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__MODIFIER;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE__NAME;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SYNTAX__NAME;
import static com.google.eclipse.protobuf.validation.Messages.expectedFieldNumber;
import static com.google.eclipse.protobuf.validation.Messages.expectedSyntaxIdentifier;
import static com.google.eclipse.protobuf.validation.Messages.fieldNumberAlreadyUsed;
import static com.google.eclipse.protobuf.validation.Messages.fieldNumbersMustBePositive;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapKeyType;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapValueType;
import static com.google.eclipse.protobuf.validation.Messages.mapWithModifier;
import static com.google.eclipse.protobuf.validation.Messages.missingModifier;
import static com.google.eclipse.protobuf.validation.Messages.multiplePackages;
import static com.google.eclipse.protobuf.validation.Messages.oneofFieldWithModifier;
import static com.google.eclipse.protobuf.validation.Messages.requiredInProto3;
import static com.google.eclipse.protobuf.validation.Messages.unknownSyntax;
import static com.google.eclipse.protobuf.validation.Messages.unrecognizedSyntaxIdentifier;
import static java.lang.String.format;

import com.google.eclipse.protobuf.grammar.Syntaxes;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MapType;
import com.google.eclipse.protobuf.protobuf.MapTypeLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.ModifierEnum;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.eclipse.protobuf.protobuf.ScalarType;
import com.google.eclipse.protobuf.protobuf.ScalarTypeLink;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.protobuf.TypeLink;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ComposedChecks;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ComposedChecks(validators = { DataTypeValidator.class, ImportValidator.class })
public class ProtobufJavaValidator extends AbstractProtobufJavaValidator {
  public static final String SYNTAX_IS_NOT_KNOWN_ERROR = "syntaxIsNotProto2";
  public static final String INVALID_FIELD_TAG_NUMBER_ERROR = "invalidFieldTagNumber";
  public static final String MORE_THAN_ONE_PACKAGE_ERROR = "moreThanOnePackage";
  public static final String MISSING_MODIFIER_ERROR = "noModifier";
  public static final String MAP_WITH_MODIFIER_ERROR = "mapWithModifier";
  public static final String REQUIRED_IN_PROTO3_ERROR = "requiredInProto3";
  public static final String INVALID_MAP_KEY_TYPE_ERROR = "invalidMapKeyType";
  public static final String MAP_WITH_MAP_VALUE_TYPE_ERROR = "mapWithMapValueType";
  public static final String ONEOF_FIELD_WITH_MODIFIER_ERROR = "oneofFieldWithModifier";

  @Inject private IndexedElements indexedElements;
  @Inject private NameResolver nameResolver;
  @Inject private Protobufs protobufs;
  @Inject private IQualifiedNameProvider qualifiedNameProvider;

  @Check public void checkIsKnownSyntax(Protobuf protobuf) {
    if (!protobufs.hasKnownSyntax(protobuf)) {
      warning(unknownSyntax, null);
    }
  }

  @Check public void checkSyntaxIsKnown(Syntax syntax) {
    String name = syntax.getName();
    if (Syntaxes.proto2().equals(name) || Syntaxes.proto3().equals(name)) {
      return;
    }
    String msg = (name == null) ? expectedSyntaxIdentifier : format(unrecognizedSyntaxIdentifier, name);
    error(msg, syntax, SYNTAX__NAME, SYNTAX_IS_NOT_KNOWN_ERROR);
  }

  @Check public void checkTagNumberIsUnique(IndexedElement e) {
    if (isNameNull(e)) {
      return; // we already show an error if name is null, no need to go further.
    }
    
    EObject container = e.eContainer();
    if (container instanceof OneOf) {
      container = container.eContainer();  
    }
    if (container instanceof Message) {
      Message message = (Message) container;
      Iterable<MessageElement> elements = message.getElements();
      checkTagNumberIsUnique(e, message, elements);
    }
  }

  @Check public void checkFieldModifiers(MessageField field) {
    if (field.getType() instanceof MapTypeLink) {
      checkMapField(field);
      return;
    }
    if (field.eContainer() instanceof OneOf) {
      checkOneOfField(field);
      return;
    }
    if (field.getModifier() == ModifierEnum.UNSPECIFIED && isProto2Field(field)) {
      error(missingModifier, field, MESSAGE_FIELD__MODIFIER, MISSING_MODIFIER_ERROR);
    } else if (field.getModifier() == ModifierEnum.REQUIRED && isProto3Field(field)) {
      error(requiredInProto3, field, MESSAGE_FIELD__MODIFIER, REQUIRED_IN_PROTO3_ERROR);
    }
  }

  private void checkMapField(MessageField field) {
    // TODO(het): Add quickfix to delete the modifier
    if (field.getModifier() != ModifierEnum.UNSPECIFIED) {
      error(mapWithModifier, field, MESSAGE_FIELD__MODIFIER, MAP_WITH_MODIFIER_ERROR);
    }
  }

  private void checkOneOfField(MessageField field) {
    if (field.getModifier() != ModifierEnum.UNSPECIFIED) {
      error(oneofFieldWithModifier, field, MESSAGE_FIELD__MODIFIER,
          ONEOF_FIELD_WITH_MODIFIER_ERROR);
    }
  }

  private boolean isProto2Field(MessageField field) {
    EObject container = field.eContainer();
    if (container != null && !(container instanceof Protobuf)) {
      container = container.eContainer();
    }
    if (container instanceof Protobuf) {
      return Syntaxes.isSpecifyingProto2Syntax(((Protobuf) container).getSyntax().getName());
    }
    return false;
  }

  private boolean isProto3Field(MessageField field) {
    EObject container = field.eContainer();
    while (container != null && !(container instanceof Protobuf)) {
      container = container.eContainer();
    }
    if (container instanceof Protobuf) {
      return Syntaxes.isSpecifyingProto3Syntax(((Protobuf) container).getSyntax().getName());
    }
    return false;
  }

  private boolean checkTagNumberIsUnique(IndexedElement e, EObject message, 
      Iterable<MessageElement> elements) {
    long index = indexedElements.indexOf(e);

    for (MessageElement element : elements) {
      if (element instanceof OneOf) {
        if (!checkTagNumberIsUnique(e, message, ((OneOf) element).getElements())) {
          return false;
        }
      } else if (element instanceof IndexedElement) {
        IndexedElement other = (IndexedElement) element;
        if (other == e) {
          return true;
        }
        if (indexedElements.indexOf(other) == index) {
          QualifiedName messageName = qualifiedNameProvider.getFullyQualifiedName(message);
          String msg = format(fieldNumberAlreadyUsed, index, messageName.toString(), nameResolver.nameOf(other));
          invalidTagNumberError(msg, e);
          return false;
        }
      }
    }
    
    return true;
  }

  @Check public void checkTagNumberIsGreaterThanZero(IndexedElement e) {
    if (isNameNull(e))
     {
      return; // we already show an error if name is null, no need to go further.
    }
    long index = indexedElements.indexOf(e);
    if (index > 0) {
      return;
    }
    String msg = (index == 0) ? fieldNumbersMustBePositive : expectedFieldNumber;
    invalidTagNumberError(msg, e);
  }

  private void invalidTagNumberError(String message, IndexedElement e) {
    error(message, e, indexedElements.indexFeatureOf(e), INVALID_FIELD_TAG_NUMBER_ERROR);
  }

  @Check public void checkOnlyOnePackageDefinition(Package aPackage) {
    boolean firstFound = false;
    Protobuf root = (Protobuf) aPackage.eContainer();
    for (ProtobufElement e : root.getElements()) {
      if (e == aPackage) {
        if (firstFound) {
          error(multiplePackages, aPackage, PACKAGE__NAME, MORE_THAN_ONE_PACKAGE_ERROR);
        }
        return;
      }
      if (e instanceof Package && !firstFound) {
        firstFound = true;
      }
    }
  }

  private boolean isNameNull(IndexedElement e) {
    return nameResolver.nameOf(e) == null;
  }

  @Check public void checkMapTypeHasValidKeyType(MapType map) {
    TypeLink keyType = map.getKeyType();
    if (!(keyType instanceof ScalarTypeLink)) {
      error(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE, INVALID_MAP_KEY_TYPE_ERROR);
      return;
    }
    ScalarType scalarKeyType = ((ScalarTypeLink) keyType).getTarget();
    if (scalarKeyType == ScalarType.BYTES || scalarKeyType == ScalarType.DOUBLE
        || scalarKeyType == ScalarType.FLOAT) {
      error(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE, INVALID_MAP_KEY_TYPE_ERROR);
    }
  }

  @Check public void checkMapTypeHasValidValueType(MapType map) {
    TypeLink keyType = map.getValueType();
    if (keyType instanceof MapTypeLink) {
      error(invalidMapValueType, map, MAP_TYPE__VALUE_TYPE, MAP_WITH_MAP_VALUE_TYPE_ERROR);
      return;
    }
  }
}

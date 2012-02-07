/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;
import static com.google.eclipse.protobuf.validation.Messages.*;
import static java.lang.String.format;

import com.google.eclipse.protobuf.grammar.Syntaxes;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.validation.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ComposedChecks(validators = { DataTypeValidator.class, ImportValidator.class })
public class ProtobufJavaValidator extends AbstractProtobufJavaValidator {
  public static final String SYNTAX_IS_NOT_PROTO2_ERROR = "syntaxIsNotProto2";
  public static final String INVALID_FIELD_TAG_NUMBER_ERROR = "invalidFieldTagNumber";
  public static final String MORE_THAN_ONE_PACKAGE_ERROR = "moreThanOnePackage";

  @Inject private IndexedElements indexedElements;
  @Inject private NameResolver nameResolver;
  @Inject private Protobufs protobufs;
  @Inject private IQualifiedNameProvider qualifiedNameProvider;

  @Check public void checkIsProto2(Protobuf protobuf) {
    if (!protobufs.isProto2(protobuf)) {
      warning(nonProto2, null);
    }
  }

  @Check public void checkSyntaxIsProto2(Syntax syntax) {
    String name = syntax.getName();
    if (Syntaxes.proto2().equals(name)) {
      return;
    }
    String msg = (name == null) ? expectedSyntaxIdentifier : format(unrecognizedSyntaxIdentifier, name);
    error(msg, syntax, SYNTAX__NAME, SYNTAX_IS_NOT_PROTO2_ERROR);
  }

  @Check public void checkTagNumberIsUnique(IndexedElement e) {
    if (isNameNull(e)) {
      return; // we already show an error if name is null, no need to go further.
    }
    long index = indexedElements.indexOf(e);
    EObject container = e.eContainer();
    if (container instanceof Message) {
      Message message = (Message) container;
      for (MessageElement element : message.getElements()) {
        if (!(element instanceof IndexedElement)) {
          continue;
        }
        IndexedElement other = (IndexedElement) element;
        if (other == e) {
          break;
        }
        if (indexedElements.indexOf(other) != index) {
          continue;
        }
        QualifiedName messageName = qualifiedNameProvider.getFullyQualifiedName(message);
        String msg = format(fieldNumberAlreadyUsed, index, messageName.toString(), nameResolver.nameOf(other));
        invalidTagNumberError(msg, e);
        break;
      }
    }
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
}

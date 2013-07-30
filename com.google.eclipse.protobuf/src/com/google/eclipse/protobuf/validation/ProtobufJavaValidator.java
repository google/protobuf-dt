/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static java.lang.String.format;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE__NAME;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SYNTAX__NAME;
import static com.google.eclipse.protobuf.validation.Messages.expectedFieldNumber;
import static com.google.eclipse.protobuf.validation.Messages.expectedSyntaxIdentifier;
import static com.google.eclipse.protobuf.validation.Messages.fieldNumberAlreadyUsed;
import static com.google.eclipse.protobuf.validation.Messages.fieldNumbersMustBePositive;
import static com.google.eclipse.protobuf.validation.Messages.multiplePackages;
import static com.google.eclipse.protobuf.validation.Messages.nonProto2;
import static com.google.eclipse.protobuf.validation.Messages.unrecognizedSyntaxIdentifier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ComposedChecks;

import com.google.eclipse.protobuf.grammar.Syntaxes;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.inject.Inject;

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
    
    EObject container = e.eContainer();
    if (container instanceof OneOf) {
      container = container.eContainer();  
    }
    if (container instanceof Message) {
      Message message = (Message) container;
      Iterable<MessageElement> elements = message.getElements();
      checkTagNumerIsUnique(e, message, elements);
    }
  }

  private boolean checkTagNumerIsUnique(IndexedElement e, EObject message, 
      Iterable<MessageElement> elements) {
    long index = indexedElements.indexOf(e);

    for (MessageElement element : elements) {
      if (element instanceof OneOf) {
        if (!checkTagNumerIsUnique(e, message, ((OneOf) element).getElements())) {
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
}

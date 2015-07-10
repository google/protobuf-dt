/*
 * Copyright (c) 2014, 2015 Google Inc.
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
import static com.google.eclipse.protobuf.validation.Messages.fieldNumbersMustBePositive;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapKeyType;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapValueType;
import static com.google.eclipse.protobuf.validation.Messages.mapWithModifier;
import static com.google.eclipse.protobuf.validation.Messages.mapWithinTypeExtension;
import static com.google.eclipse.protobuf.validation.Messages.missingModifier;
import static com.google.eclipse.protobuf.validation.Messages.multiplePackages;
import static com.google.eclipse.protobuf.validation.Messages.nameConflict;
import static com.google.eclipse.protobuf.validation.Messages.oneofFieldWithModifier;
import static com.google.eclipse.protobuf.validation.Messages.requiredInProto3;
import static com.google.eclipse.protobuf.validation.Messages.reservedIndexAndName;
import static com.google.eclipse.protobuf.validation.Messages.reservedToMax;
import static com.google.eclipse.protobuf.validation.Messages.tagNumberRangeConflict;
import static com.google.eclipse.protobuf.validation.Messages.tagNumberConflict;
import static com.google.eclipse.protobuf.validation.Messages.conflictingExtensions;
import static com.google.eclipse.protobuf.validation.Messages.conflictingField;
import static com.google.eclipse.protobuf.validation.Messages.conflictingGroup;
import static com.google.eclipse.protobuf.validation.Messages.conflictingReservedName;
import static com.google.eclipse.protobuf.validation.Messages.conflictingReservedNumber;
import static com.google.eclipse.protobuf.validation.Messages.unknownSyntax;
import static com.google.eclipse.protobuf.validation.Messages.unrecognizedSyntaxIdentifier;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.eclipse.protobuf.model.util.IndexRanges;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.StringLiterals;
import com.google.eclipse.protobuf.model.util.Syntaxes;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.Extensions;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexRange;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MapType;
import com.google.eclipse.protobuf.protobuf.MapTypeLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.ModifierEnum;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.eclipse.protobuf.protobuf.ProtobufPackage;
import com.google.eclipse.protobuf.protobuf.Reservation;
import com.google.eclipse.protobuf.protobuf.Reserved;
import com.google.eclipse.protobuf.protobuf.ScalarType;
import com.google.eclipse.protobuf.protobuf.ScalarTypeLink;
import com.google.eclipse.protobuf.protobuf.StringLiteral;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.eclipse.protobuf.protobuf.TypeLink;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.util.SimpleAttributeResolver;
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
  @Inject private IndexRanges indexRanges;
  @Inject private NameResolver nameResolver;
  @Inject private StringLiterals stringLiterals;
  @Inject private Protobufs protobufs;
  @Inject private Syntaxes syntaxes;

  @Check public void checkIsKnownSyntax(Protobuf protobuf) {
    if (!protobufs.hasKnownSyntax(protobuf)) {
      warning(unknownSyntax, null);
    }
  }

  @Check public void checkSyntaxIsKnown(Syntax syntax) {
    if (syntaxes.isSpecifyingProto2Syntax(syntax) || syntaxes.isSpecifyingProto3Syntax(syntax)) {
      return;
    }
    String name = syntaxes.getName(syntax);
    String msg = (name == null) ? expectedSyntaxIdentifier : format(unrecognizedSyntaxIdentifier, name);
    error(msg, syntax, SYNTAX__NAME, SYNTAX_IS_NOT_KNOWN_ERROR);
  }

  @Check public void checkForIndexConflicts(Message message) {
    Multimap<EObject, Range<Long>> rangeUsages = LinkedHashMultimap.create();

    for (Reserved reserved : getOwnedElements(message, Reserved.class)) {
      for (IndexRange indexRange : Iterables.filter(reserved.getReservations(), IndexRange.class)) {
        Range<Long> range = indexRanges.toLongRange(indexRange);
        errorOnConflicts(range, rangeUsages, indexRange, null);
        rangeUsages.put(reserved, range);
      }
    }

    for (Extensions extensions : getOwnedElements(message, Extensions.class)) {
      for (IndexRange indexRange : extensions.getRanges()) {
        Range<Long> range = indexRanges.toLongRange(indexRange);
        errorOnConflicts(range, rangeUsages, indexRange, null);
        rangeUsages.put(extensions, range);
      }
    }

    for (IndexedElement element : getOwnedElements(message, IndexedElement.class)) {
      long index = indexedElements.indexOf(element);
      Range<Long> range = Range.singleton(index);
      EStructuralFeature feature = indexedElements.indexFeatureOf(element);
      errorOnConflicts(range, rangeUsages, element, feature);
      rangeUsages.put(element, range);
    }
  }

  private void errorOnConflicts(
      Range<Long> range,
      Multimap<EObject, Range<Long>> rangeUsages,
      EObject errorSource,
      EStructuralFeature errorFeature) {
    for (Map.Entry<EObject, Range<Long>> rangeUsage : rangeUsages.entries()) {
      Range<Long> usedRange = rangeUsage.getValue();
      if (range.isConnected(usedRange)) {
        EObject rangeUser = rangeUsage.getKey();

        boolean rangeIsSingular =
            range.hasUpperBound() && range.upperEndpoint() == range.lowerEndpoint();
        String template = rangeIsSingular ? tagNumberConflict : tagNumberRangeConflict;

        String rangeUserString;
        String usedRangeString = rangeToString(usedRange);
        if (rangeUser instanceof MessageField) {
          rangeUserString =
              String.format(conflictingField, nameResolver.nameOf(rangeUser), usedRangeString);
        } else if (rangeUser instanceof Group) {
          rangeUserString =
              String.format(conflictingGroup, nameResolver.nameOf(rangeUser), usedRangeString);
        } else if (rangeUser instanceof Reserved) {
          rangeUserString = String.format(conflictingReservedNumber, usedRangeString);
        } else {
          rangeUserString = String.format(conflictingExtensions, usedRangeString);
        }

        String message = String.format(template, rangeToString(range), rangeUserString);
        error(message, errorSource, errorFeature);

        // Don't report more than one error per element.
        return;
      }
    }
  }

  private String rangeToString(Range<Long> range) {
    if (range.hasLowerBound() && range.hasUpperBound()
        && range.lowerEndpoint() == range.upperEndpoint()) {
      return String.valueOf(range.lowerEndpoint());
    }

    String upper =
        range.hasUpperBound() ? String.valueOf(range.upperEndpoint()) : indexRanges.getMaxKeyword();
    return String.format("%d to %s", range.lowerEndpoint(), upper);
  }

  @Check public void checkForReservedToMax(Reserved reserved) {
    for (IndexRange range : Iterables.filter(reserved.getReservations(), IndexRange.class)) {
      String to = range.getTo();
      if (indexRanges.getMaxKeyword().equals(to)) {
        error(reservedToMax, range, ProtobufPackage.Literals.INDEX_RANGE__TO);
      }
    }
  }

  @Check public void checkForReservedNameConflicts(Message message) {
    Set<String> reservedNames = new HashSet<>();
    for (Reserved reserved : getOwnedElements(message, Reserved.class)) {
      for (StringLiteral stringLiteral :
          Iterables.filter(reserved.getReservations(), StringLiteral.class)) {
        String name = stringLiterals.getCombinedString(stringLiteral);
        reportReservedNameConflicts(name, reservedNames, stringLiteral, null);
        reservedNames.add(name);
      }
    }

    for (IndexedElement element : getOwnedElements(message, IndexedElement.class)) {
      String name = nameResolver.nameOf(element);
      if (name != null) {
        EAttribute nameAttribute = SimpleAttributeResolver.NAME_RESOLVER.getAttribute(element);
        reportReservedNameConflicts(name, reservedNames, element, nameAttribute);
      }
    }
  }

  @Check public void checkForReservedIndexAndName(Reserved reserved) {
    boolean hasIndexReservation = false;
    boolean hasNameReservation = false;
    for (Reservation reservation : reserved.getReservations()) {
      if (reservation instanceof IndexRange) {
        hasIndexReservation = true;
      } else if (reservation instanceof StringLiteral) {
        hasNameReservation = true;
      }
    }

    if (hasIndexReservation && hasNameReservation) {
      error(reservedIndexAndName, reserved, null);
    }
  }

  private void reportReservedNameConflicts(
      String name, Set<String> reservedNames, EObject errorSource, EAttribute errorFeature) {
    if (reservedNames.contains(name)) {
      String nameUser = String.format(conflictingReservedName, name);
      String message = String.format(nameConflict, name, nameUser);
      error(message, errorSource, errorFeature);
    }
  }

  /**
   * Returns elements of the given type contained within the given message, except those contained
   * within intervening Messages or TypeExtensions.
   */
  private static <E extends EObject> Collection<E> getOwnedElements(
      Message container, Class<E> elementType) {
    Collection<E> elements = new ArrayList<E>();

    TreeIterator<EObject> elementsIterator = container.eAllContents();
    while (elementsIterator.hasNext()) {
      EObject element = elementsIterator.next();

      if (elementType.isAssignableFrom(element.getClass())) {
        @SuppressWarnings("unchecked")
        E elementAsElementType = (E) element;
        elements.add(elementAsElementType);
      }

      if (element instanceof Message || element instanceof TypeExtension) {
        elementsIterator.prune();
      }
    }

    return elements;
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
      return syntaxes.isSpecifyingProto2Syntax(((Protobuf) container).getSyntax());
    }
    return false;
  }

  private boolean isProto3Field(MessageField field) {
    EObject container = field.eContainer();
    while (container != null && !(container instanceof Protobuf)) {
      container = container.eContainer();
    }
    if (container instanceof Protobuf) {
      return syntaxes.isSpecifyingProto3Syntax(((Protobuf) container).getSyntax());
    }
    return false;
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

  @Check public void checkMapIsNotWithinExtension(MapType map) {
    if (EcoreUtil2.getContainerOfType(map, TypeExtension.class) != null) {
      error(mapWithinTypeExtension, map, null);
    }
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.*;
import com.google.inject.Inject;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping">Xtext Scoping</a>
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ProtobufElementFinder finder;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private Options options;
  @Inject private TypeDescriptions typeDescriptions;

  @SuppressWarnings("unused")
  IScope scope_TypeRef_type(TypeRef typeRef, EReference reference) {
    Protobuf root = finder.rootOf(typeRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject current = typeRef.eContainer().eContainer(); // get message of the property containing the TypeReference
    Class<Type> targetType = Type.class;
    while (current != null) {
      descriptions.addAll(typeDescriptions.localTypes(current, targetType));
      current = current.eContainer();
    }
    descriptions.addAll(typeDescriptions.importedTypes(root, targetType));
    return createScope(descriptions);
  }

  @SuppressWarnings("unused")
  IScope scope_MessageRef_type(MessageRef messageRef, EReference reference) {
    Protobuf root = finder.rootOf(messageRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Class<Message> targetType = Message.class;
    descriptions.addAll(typeDescriptions.localTypes(root, targetType));
    descriptions.addAll(typeDescriptions.importedTypes(root, targetType));
    return createScope(descriptions);
  }

  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject container = literalRef.eContainer();
    Enum anEnum = null;
    if (container instanceof BuiltInOption) {
      Property p = options.propertyFrom((Option) container);
      anEnum = descriptorProvider.get().enumTypeOf(p);
    }
    if (container instanceof Property) {
      anEnum = finder.enumTypeOf((Property) container);
    }
    if (container instanceof BuiltInFieldOption) {
      BuiltInFieldOption option = (BuiltInFieldOption) container;
      if (fieldOptions.isDefaultValueOption(option)) {
        Property property = (Property) option.eContainer();
        anEnum = finder.enumTypeOf(property);
      } else {
        anEnum = descriptorProvider.get().enumTypeOf(option);
      }
    }
    if (anEnum != null) return createScope(literalDescriptions.literalsOf(anEnum));
    return null;
  }

  @SuppressWarnings("unused")
  IScope scope_PropertyRef_property(PropertyRef propertyRef, EReference reference) {
    EObject mayBeOption = propertyRef.eContainer();
    if (mayBeOption instanceof BuiltInOption) {
      ProtoDescriptor descriptor = descriptorProvider.get();
      EObject optionContainer = mayBeOption.eContainer();
      Collection<Property> propertyOptions = descriptor.availableOptionsFor(optionContainer);
      if (!propertyOptions.isEmpty()) return createScope(describe(propertyOptions));
    }
    List<IEObjectDescription> descriptions = Collections.emptyList();
    // return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
    return null;
  }

  private Collection<IEObjectDescription> describe(Collection<Property> properties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : properties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

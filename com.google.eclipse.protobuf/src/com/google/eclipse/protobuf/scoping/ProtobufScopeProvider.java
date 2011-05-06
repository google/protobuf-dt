/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.ProtobufElementFinder;
import com.google.inject.Inject;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private ProtobufElementFinder finder;
  @Inject private Globals globals;
  @Inject private IQualifiedNameProvider nameProvider;
  @Inject private ImportUriResolver uriResolver;
  @Inject private AlternativeQualifiedNamesProvider alternativeNamesProvider;

  @SuppressWarnings("unused")
  IScope scope_TypeReference_type(TypeReference typeRef, EReference reference) {
    Protobuf root = finder.rootOf(typeRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject message = typeRef.eContainer().eContainer();
    descriptions.addAll(innerTypes(message));
    descriptions.addAll(innerTypes(message.eContainer()));
    descriptions.addAll(innerTypes(root));
    descriptions.addAll(importedTypes(root, Type.class));
    return createScope(descriptions);
  }

  private Collection<IEObjectDescription> innerTypes(EObject root) {
    return innerTypes(root, Type.class);
  }

  @SuppressWarnings("unused")
  IScope scope_MessageReference_type(MessageReference msgRef, EReference reference) {
    Protobuf root = finder.rootOf(msgRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Class<Message> targetType = Message.class;
    descriptions.addAll(innerTypes(root, targetType));
    descriptions.addAll(importedTypes(root, targetType));
    return createScope(descriptions);
  }

  private <T extends Type> Collection<IEObjectDescription> innerTypes(EObject root, Class<T> targetType) {
    return innerTypes(root, targetType, 0);
  }

  private <T extends Type> Collection<IEObjectDescription> innerTypes(EObject root, Class<T> targetType, int level) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (!targetType.isInstance(element)) continue;
      T type = targetType.cast(element);
      List<QualifiedName> names = alternativeNamesProvider.alternativeFullyQualifiedNames(type);
      int nameCount = names.size();
      for (int i = level; i < nameCount; i++) descriptions.add(create(names.get(i), type));
      descriptions.add(create(nameProvider.getFullyQualifiedName(type), type));
      if (!(element instanceof Message)) continue;
      descriptions.addAll(innerTypes(element, targetType, level + 1));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> importedTypes(Protobuf root, Class<T> targetType) {
    ResourceSet resourceSet = root.eResource().getResourceSet();
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : root.getImports()) {
      URI importUri = createURI(uriResolver.apply(anImport));
      Resource imported = resourceSet.getResource(importUri, true);
      descriptions.addAll(innerTypes(imported, targetType));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> innerTypes(Resource resource, Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!targetType.isInstance(next)) continue;
      T type = targetType.cast(next);
      descriptions.add(create(nameProvider.getFullyQualifiedName(type), type));
    }
    return descriptions;
  }

  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject container = literalRef.eContainer();
    if (container instanceof Property) {
      Enum enumType = finder.enumTypeOf((Property) container);
      if (enumType != null) return scopeForLiterals(enumType);
    }
    if (container instanceof Option && globals.isOptimizeForOption((Option) container)) {
      Enum optimizedMode = globals.optimizedMode();
      return scopeForLiterals(optimizedMode);
    }
    return null;
  }

  private static IScope scopeForLiterals(Enum enumType) {
    Collection<IEObjectDescription> descriptions = describeLiterals(enumType);
    return createScope(descriptions);
  }

  private static Collection<IEObjectDescription> describeLiterals(Enum enumType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : enumType.getLiterals())
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

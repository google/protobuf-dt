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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
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
  
  @SuppressWarnings("unused")
  IScope scope_TypeReference_type(TypeReference typeRef, EReference reference) {
    Protobuf root = finder.rootOf(typeRef);
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    Message message = (Message) typeRef.eContainer().eContainer();
    addAllTypeDescriptionsInsideRoot(message, descriptions);
    descriptions.addAll(importedTypes(root));
    return createScope(descriptions);
  }

  private void addAllTypeDescriptionsInsideRoot(Message root, List<IEObjectDescription> descriptions) {
    TreeIterator<EObject> allContents = root.eAllContents();
    while (allContents.hasNext()) {
      EObject element = allContents.next();
      if (!(element instanceof Type)) continue;
      Type type = (Type) element;
      descriptions.addAll(describeUsingQualifiedNames(type));
      descriptions.add(create(type.getName(), type));
    }
  }
  
  private List<IEObjectDescription> describeUsingQualifiedNames(Type type) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    QualifiedName fqn = nameProvider.getFullyQualifiedName(type);
    descriptions.add(create(fqn, type));
    QualifiedName fqnWithoutPackage = removePackage(fqn, type);
    if (fqnWithoutPackage != null) descriptions.add(create(fqnWithoutPackage, type));
    return descriptions;
  }

  private QualifiedName removePackage(QualifiedName fqn, Type type) {
    Package aPackage = finder.packageOf(type);
    if (aPackage == null) return null;
    return null;
  }

  private List<IEObjectDescription> importedTypes(Protobuf root) {
    ResourceSet resourceSet = root.eResource().getResourceSet();
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : root.getImports()) {
      URI importUri = createURI(uriResolver.apply(anImport));
      Resource imported = resourceSet.getResource(importUri, true);
      descriptions.addAll(describeTypesUsingQualifiedNames(imported));
    }
    return descriptions;
  }

  private List<IEObjectDescription> describeTypesUsingQualifiedNames(Resource resource) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!(next instanceof Type)) continue;
      Type type = (Type) next;
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
    List<IEObjectDescription> descriptions = describeLiterals(enumType);
    return createScope(descriptions);
  }

  private static List<IEObjectDescription> describeLiterals(Enum enumType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : enumType.getLiterals())
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }

  private static IScope createScope(List<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

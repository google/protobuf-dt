/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.emf.common.util.URI.createFileURI;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.LiteralRef;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Type;
import com.google.eclipse.protobuf.protobuf.TypeReference;
import com.google.eclipse.protobuf.util.EObjectFinder;
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

  @Inject private EObjectFinder finder;
  @Inject private Globals globals;
  @Inject private IQualifiedNameProvider nameProvider;
  @Inject private ImportUriResolver uriResolver;
  
  @SuppressWarnings("unused")
  IScope scope_TypeReference_type(TypeReference typeRef, EReference reference) {
    Protobuf root = finder.rootOf(typeRef);
    resolveImports(root);
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Type type : getAllContentsOfType(root, Type.class)) {
      descriptions.add(create(nameProvider.getFullyQualifiedName(type), type));
      descriptions.add(create(type.getName(), type));
    }
    return createScope(descriptions);
  }

  private void resolveImports(Protobuf root) {
    EList<Import> imports = root.getImports();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    for (Import imp : imports) {
      String importURI = uriResolver.apply(imp);
      resourceSet.getResource(createFileURI(importURI), true);
    }
  }
  
  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject container = literalRef.eContainer();
    if (container instanceof Property) {
      Enum enumType = finder.enumTypeOf((Property) container);
      if (enumType != null) return scopeForLiteralsIn(enumType);
    }
    if (container instanceof Option && globals.isOptimizeForOption((Option) container)) {
      Enum optimizedMode = globals.optimizedMode();
      return scopeForLiteralsIn(optimizedMode);
    }
    return null;
  }

  private static IScope scopeForLiteralsIn(Enum enumType) {
    List<IEObjectDescription> descriptions = literalDescriptions(enumType);
    return createScope(descriptions);
  }

  private static List<IEObjectDescription> literalDescriptions(Enum enumType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : enumType.getLiterals())
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }

  private static IScope createScope(List<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

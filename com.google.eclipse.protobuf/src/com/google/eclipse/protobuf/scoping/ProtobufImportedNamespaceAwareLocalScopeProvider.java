/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.singletonList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportScope;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.util.Strings;

import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameConverter;
import com.google.inject.Inject;

/**
 * A local scope provider for the Protobuf language that
 * understands namespace imports.
 *
 * @author (atrookey@google.com) Alexander Rookey
 */
public class ProtobufImportedNamespaceAwareLocalScopeProvider
    extends ImportedNamespaceAwareLocalScopeProvider {
  @Inject private ProtobufQualifiedNameConverter qualifiedNameConverter;
  @Inject private IGlobalScopeProvider globalScopeProvider;

  private static final boolean WILDCARD = true;

  @Override
  protected ImportScope createImportScope(
      IScope parent,
      List<ImportNormalizer> namespaceResolvers,
      ISelectable importFrom,
      EClass type,
      boolean ignoreCase) {
    return new ProtobufImportScope(namespaceResolvers, parent, importFrom, type, ignoreCase);
  }

  @Override
  protected List<ImportNormalizer> internalGetImportedNamespaceResolvers(
      final EObject context, boolean ignoreCase) {
    List<ImportNormalizer> importedNamespaceResolvers = new ArrayList<>();
    EList<EObject> eContents = context.eContents();
    for (EObject child : eContents) {
      String name = getImportedNamespace(child);
      if (name != null && !name.isEmpty()) {
        ImportNormalizer resolver = createImportedNamespaceResolver(name, ignoreCase);
        if (resolver != null) {
          importedNamespaceResolvers.add(resolver);
        }
        importedNamespaceResolvers.addAll(createResolversForInnerNamespaces(name, ignoreCase));
      }
    }
    return importedNamespaceResolvers;
  }

  /**
   * Creates resolvers required for scoping to handle intersecting packages. The imported namespace
   * {@code com.google.proto.foo} requires the following resolvers:
   *
   * <ul>
   * <li>{@code com.*}
   * <li>{@code com.google.*}
   * <li>{@code com.google.proto.*}
   * </ul>
   *
   * @param namespace the namespace.
   * @param ignoreCase {@code true} if the resolver should be case insensitive.
   * @return a list of the resolvers for an imported namespace
   */
  private List<ImportNormalizer> createResolversForInnerNamespaces(
      String namespace, boolean ignoreCase) {
    String[] splitValue = namespace.split("\\.");
    List<ImportNormalizer> importedNamespaceResolvers = new ArrayList<>();
    String currentNamespaceResolver = "";
    for (int i = 0; i < Array.getLength(splitValue) - 1; i++) {
      currentNamespaceResolver += splitValue[i] + ".";
      ImportNormalizer resolver =
          createImportedNamespaceResolver(currentNamespaceResolver, ignoreCase);
      if (resolver != null) {
        importedNamespaceResolvers.add(resolver);
      }
    }
    return importedNamespaceResolvers;
  }

  /** Creates an {@link ImportNormalizer} with wildcards. */
  @Override
  protected ImportNormalizer createImportedNamespaceResolver(String namespace, boolean ignoreCase) {
    if (Strings.isEmpty(namespace)) {
      return null;
    }
    QualifiedName importedNamespace = qualifiedNameConverter.toQualifiedName(namespace);
    if (importedNamespace == null || importedNamespace.isEmpty()) {
      return null;
    }
    return doCreateImportNormalizer(importedNamespace, WILDCARD, ignoreCase);
  }

  /**
   * Creates a {@link ProtobufImportScope} regardless of whether or not
   * {@code namespaceResolvers} is empty.
   */
  @Override
  protected IScope getLocalElementsScope(
      IScope parent, final EObject context, final EReference reference) {
    IScope result = parent;
    ISelectable allDescriptions = getAllDescriptions(context.eResource());
    QualifiedName name = getQualifiedNameOfLocalElement(context);
    boolean ignoreCase = isIgnoreCase(reference);
    final List<ImportNormalizer> namespaceResolvers =
        getImportedNamespaceResolvers(context, ignoreCase);
    if (isRelativeImport() && name != null && !name.isEmpty()) {
      ImportNormalizer localNormalizer = doCreateImportNormalizer(name, true, ignoreCase);
      result =
          createImportScope(
              result,
              singletonList(localNormalizer),
              allDescriptions,
              reference.getEReferenceType(),
              isIgnoreCase(reference));
    }
    result =
        createImportScope(
            result,
            namespaceResolvers,
            null,
            reference.getEReferenceType(),
            isIgnoreCase(reference));
    if (name != null) {
      ImportNormalizer localNormalizer = doCreateImportNormalizer(name, true, ignoreCase);
      result =
          createImportScope(
              result,
              singletonList(localNormalizer),
              allDescriptions,
              reference.getEReferenceType(),
              isIgnoreCase(reference));
    }
    return result;
  }

  /** 
   * Makes {@code getAllDescriptions()} visible to {@link ProtobufScopeProvider}
   */
  @Override
  protected ISelectable getAllDescriptions(Resource resource) {
    return super.getAllDescriptions(resource);
  }
  /** 
   * Makes {@code getImportedNamespaceResolvers()} visible to
   * {@link ProtobufScopeProvider}
   */
  @Override
  protected List<ImportNormalizer> getImportedNamespaceResolvers(
      EObject context, boolean ignoreCase) {
    return super.getImportedNamespaceResolvers(context, ignoreCase);
  }
  /** 
   * Makes {@code getResourceScope()} visible to {@link ProtobufScopeProvider}
   */
  @Override
  protected IScope getResourceScope(Resource res, EReference reference) {
    return super.getResourceScope(res, reference);
  }

  /** Returns a {@link ProtobufSelectableBasedScope} instead of {@link SelectableBasedScope} */
  @Override
  protected IScope getResourceScope(IScope parent, EObject context, EReference reference) {
    if (context.eResource() == null) {
      return parent;
    }
    ISelectable allDescriptions = getAllDescriptions(context.eResource());
    return ProtobufSelectableBasedScope.createScope(
        parent, allDescriptions, reference.getEReferenceType(), isIgnoreCase(reference));
  }

  protected ProtobufImportUriGlobalScopeProvider getGlobalScopeProvider() {
    return (ProtobufImportUriGlobalScopeProvider) globalScopeProvider;
  }
}

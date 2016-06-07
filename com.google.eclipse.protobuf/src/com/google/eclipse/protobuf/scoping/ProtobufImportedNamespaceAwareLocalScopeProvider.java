/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportScope;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.util.Strings;

import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameConverter;
import com.google.inject.Inject;

public class ProtobufImportedNamespaceAwareLocalScopeProvider
    extends ImportedNamespaceAwareLocalScopeProvider {
  @Inject private ProtobufQualifiedNameConverter qualifiedNameConverter;

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
      String namespace = getImportedNamespace(child);
      if (namespace != null) {
        ImportNormalizer resolver = createImportedNamespaceResolver(namespace, ignoreCase);
        if (resolver != null) {
          importedNamespaceResolvers.add(resolver);
        }
        importedNamespaceResolvers.addAll(createResolversForInnerNamespaces(namespace, ignoreCase));
      }
    }
    return importedNamespaceResolvers;
  }

  /**
   * Creates resolvers required for scoping to handle intersecting packages. The
   * imported namespace {@code com.google.proto.foo} requires the following
   * resolvers:
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

  /**
   * Creates a new {@link ImportNormalizer} for the given namespace.
   *
   * @param namespace the namespace.
   * @param ignoreCase {@code true} if the resolver should be case insensitive.
   * @return a new {@link ImportNormalizer} or {@code null} if the namespace
   *         cannot be converted to a valid qualified name.
   */
  @Override
  protected ImportNormalizer createImportedNamespaceResolver(String namespace, boolean ignoreCase) {
    if (Strings.isEmpty(namespace)) {
      return null;
    }
    QualifiedName importedNamespace = qualifiedNameConverter.toQualifiedName(namespace);
    if (importedNamespace == null || importedNamespace.isEmpty()) {
      return null;
    }
    return doCreateImportNormalizer(importedNamespace, true, ignoreCase);
  }
}

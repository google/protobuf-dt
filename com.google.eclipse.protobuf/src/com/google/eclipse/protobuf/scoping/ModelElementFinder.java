/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.resource.ResourceSets;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ModelElementFinder {
  @Inject private Imports imports;
  @Inject private ModelObjects modelObjects;
  @Inject private Packages packages;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;
  @Inject private ResourceSets resourceSets;

  // Start at an element
  <T> Collection<IEObjectDescription> find(EObject start, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(getDescriptionsFromObjectAncestors(start, strategy, criteria));
    Protobuf root = modelObjects.rootOf(start);
    descriptions.addAll(getDescriptionsFromAllImports(root, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private <T> Collection<IEObjectDescription> getDescriptionsFromObjectAncestors(EObject start, FinderStrategy<T> strategy, T criteria) {
    UniqueDescriptions descriptions = new UniqueDescriptions();
    EObject current = start.eContainer();
    while (current != null) {
      descriptions.addAll(getDescriptionsFromObjectDescendants(current, strategy, criteria, 0));
      current = current.eContainer();
    }
    return descriptions.values();
  }

  <T> Collection<IEObjectDescription> find(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(getDescriptionsFromObjectDescendants(start, strategy, criteria, 0));
    descriptions.addAll(getDescriptionsFromAllImports(start, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private <T> Collection<IEObjectDescription> getDescriptionsFromObjectDescendants(EObject start, FinderStrategy<T> strategy, T criteria, int level) {
    UniqueDescriptions descriptions = new UniqueDescriptions();
    for (EObject element : start.eContents()) {
      descriptions.addAll(strategy.local(element, criteria, level));
      if (element instanceof Message || element instanceof Group) {
        descriptions.addAll(getDescriptionsFromObjectDescendants(element, strategy, criteria, level + 1));
      }
    }
    return descriptions.values();
  }

  private <T> Collection<IEObjectDescription> getDescriptionsFromAllImports(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    List<Import> allImports = protobufs.importsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return getDescriptionsFromImports(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private <T> Collection<IEObjectDescription> getDescriptionsFromImports(List<Import> allImports, Package fromImporter,
      ResourceSet resourceSet, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(strategy.inDescriptor(anImport, criteria));
        continue;
      }
      URI resolvedUri = imports.resolvedUriOf(anImport);
      if (resolvedUri == null) {
        continue;
      }
      Resource imported = resourceSets.findResource(resourceSet, resolvedUri);
      if (imported == null) {
        continue;
      }
      Protobuf rootOfImported = resources.rootOf(imported);
      if (!protobufs.hasKnownSyntax(rootOfImported)) {
        continue;
      }
      if (rootOfImported != null) {
        descriptions.addAll(getDescriptionsFromPublicImports(rootOfImported, strategy, criteria));
        if (arePackagesRelated(fromImporter, rootOfImported)) {
          descriptions.addAll(getDescriptionsFromObjectDescendants(rootOfImported, strategy, criteria, 0));
          continue;
        }
        Package packageOfImported = modelObjects.packageOf(rootOfImported);
        TreeIterator<Object> contents = getAllContents(imported, true);
        while (contents.hasNext()) {
          Object next = contents.next();
          descriptions.addAll(strategy.imported(fromImporter, packageOfImported, next, criteria));
        }
      }
    }
    return descriptions;
  }

  private <T> Collection<IEObjectDescription> getDescriptionsFromPublicImports(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    if (!protobufs.hasKnownSyntax(start)) {
      return emptySet();
    }
    List<Import> allImports = protobufs.publicImportsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return getDescriptionsFromImports(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = modelObjects.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  static interface FinderStrategy<T> {
    Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target, T criteria);

    Collection<IEObjectDescription> inDescriptor(Import anImport, T criteria);

    Collection<IEObjectDescription> local(Object target, T criteria, int level);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.*;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
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

  Collection<IEObjectDescription> find(EObject start, FinderStrategy strategy, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(local(start, strategy, criteria));
    Protobuf root = modelObjects.rootOf(start);
    descriptions.addAll(imported(root, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private Collection<IEObjectDescription> local(EObject start, FinderStrategy strategy, Object criteria) {
    UniqueDescriptions descriptions = new UniqueDescriptions();
    EObject current = start.eContainer();
    while (current != null) {
      descriptions.addAll(local(current, strategy, criteria, 0));
      current = current.eContainer();
    }
    return descriptions.values();
  }

  Collection<IEObjectDescription> find(Protobuf start, FinderStrategy strategy, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(local(start, strategy, criteria, 0));
    descriptions.addAll(imported(start, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private Collection<IEObjectDescription> local(EObject start, FinderStrategy strategy, Object criteria, int level) {
    UniqueDescriptions descriptions = new UniqueDescriptions();
    for (EObject element : start.eContents()) {
      descriptions.addAll(strategy.local(element, criteria, level));
      if (element instanceof Message || element instanceof Group) {
        descriptions.addAll(local(element, strategy, criteria, level + 1));
      }
    }
    return descriptions.values();
  }

  private Collection<IEObjectDescription> imported(Protobuf start, FinderStrategy strategy, Object criteria) {
    List<Import> allImports = protobufs.importsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private Collection<IEObjectDescription> imported(List<Import> allImports, Package fromImporter,
      ResourceSet resourceSet, FinderStrategy strategy, Object criteria) {
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
      if (!protobufs.isProto2(rootOfImported)) {
        continue;
      }
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, strategy, criteria));
        if (arePackagesRelated(fromImporter, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, strategy, criteria, 0));
          continue;
        }
        Package packageOfImported = modelObjects.packageOf(rootOfImported);
        descriptions.addAll(imported(fromImporter, packageOfImported, imported, strategy, criteria));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> publicImported(Protobuf start, FinderStrategy strategy, Object criteria) {
    if (!protobufs.isProto2(start)) {
      return emptySet();
    }
    List<Import> allImports = protobufs.publicImportsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = modelObjects.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Resource resource,
      FinderStrategy strategy, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      descriptions.addAll(strategy.imported(fromImporter, fromImported, next, criteria));
    }
    return descriptions;
  }

  static interface FinderStrategy {
    Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target, Object criteria);

    Collection<IEObjectDescription> inDescriptor(Import anImport, Object criteria);

    Collection<IEObjectDescription> local(Object target, Object criteria, int level);
  }
}

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

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
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

  Collection<IEObjectDescription> find(EObject start, FinderDelegate finderDelegate, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    EObject current = start.eContainer();
    while (current != null) {
      descriptions.addAll(local(current, finderDelegate, criteria));
      current = current.eContainer();
    }
    Protobuf root = modelObjects.rootOf(start);
    descriptions.addAll(imported(root, finderDelegate, criteria));
    return unmodifiableSet(descriptions);
  }

  Collection<IEObjectDescription> find(Protobuf start, FinderDelegate finderDelegate, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(local(start, finderDelegate, criteria));
    descriptions.addAll(imported(start, finderDelegate, criteria));
    return unmodifiableSet(descriptions);
  }

  private Collection<IEObjectDescription> local(EObject start, FinderDelegate finderDelegate, Object criteria) {
    return local(start, finderDelegate, criteria, 0);
  }

  private Collection<IEObjectDescription> local(EObject start, FinderDelegate finder, Object criteria, int level) {
    Set<IEObjectDescription> descriptions = newHashSet();
    for (EObject element : start.eContents()) {
      descriptions.addAll(finder.local(element, criteria, level));
      if (element instanceof Message || element instanceof Group) {
        descriptions.addAll(local(element, finder, criteria, level + 1));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> imported(Protobuf start, FinderDelegate finderDelegate, Object criteria) {
    List<Import> allImports = protobufs.importsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, finderDelegate, criteria);
  }

  private Collection<IEObjectDescription> imported(List<Import> allImports, Package fromImporter,
      ResourceSet resourceSet, FinderDelegate finderDelegate, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(finderDelegate.inDescriptor(anImport, criteria));
        continue;
      }
      Resource imported = resources.importedResource(anImport, resourceSet);
      if (imported == null) {
        continue;
      }
      Protobuf rootOfImported = resources.rootOf(imported);
      if (!protobufs.isProto2(rootOfImported)) {
        continue;
      }
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, finderDelegate, criteria));
        if (arePackagesRelated(fromImporter, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, finderDelegate, criteria));
          continue;
        }
        Package packageOfImported = modelObjects.packageOf(rootOfImported);
        descriptions.addAll(imported(fromImporter, packageOfImported, imported, finderDelegate, criteria));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> publicImported(Protobuf start, FinderDelegate finderDelegate,
      Object criteria) {
    if (!protobufs.isProto2(start)) {
      return emptySet();
    }
    List<Import> allImports = protobufs.publicImportsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, finderDelegate, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = modelObjects.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Resource resource,
      FinderDelegate finder, Object criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      descriptions.addAll(finder.imported(fromImporter, fromImported, next, criteria));
    }
    return descriptions;
  }

  static interface FinderDelegate {
    Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target, Object criteria);

    Collection<IEObjectDescription> inDescriptor(Import anImport, Object criteria);

    Collection<IEObjectDescription> local(Object target, Object criteria, int level);
  }
}

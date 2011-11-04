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
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.parser.NonProto2;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ScopeFinder {

  @Inject private ModelFinder finder;
  @Inject private Imports imports;
  @Inject private Packages packages;
  @Inject private Resources resources;

  Collection<IEObjectDescription> findScope(EObject target, SearchDelegate delegate, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject current = target.eContainer();
    while (current != null) {
      descriptions.addAll(local(current, delegate, criteria));
      current = current.eContainer();
    }
    Protobuf root = finder.rootOf(target);
    descriptions.addAll(imported(root, delegate, criteria));
    return descriptions;
  }
  
  Collection<IEObjectDescription> findScope(Protobuf root, SearchDelegate delegate, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    descriptions.addAll(local(root, delegate, criteria));
    descriptions.addAll(imported(root, delegate, criteria));
    return descriptions;
  }

  private Collection<IEObjectDescription> local(EObject root, SearchDelegate delegate, Object criteria) {
    return local(root, delegate, criteria, 0);
  }

  private Collection<IEObjectDescription> local(EObject root, SearchDelegate delegate, Object criteria, int level) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      descriptions.addAll(delegate.descriptions(element, criteria, level));
      if (delegate.continueSearchOneLevelHigher(element)) {
        descriptions.addAll(local(element, delegate, criteria, level + 1));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> imported(Protobuf root, SearchDelegate delegate, Object criteria) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, delegate, criteria);
  }

  private Collection<IEObjectDescription> imported(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, SearchDelegate delegate, Object criteria) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(delegate.fromProtoDescriptor(anImport, criteria));
        continue;
      }
      Resource importedResource = resources.importedResource(anImport, resourceSet);
      Protobuf rootOfImported = finder.rootOf(importedResource);
      if (rootOfImported instanceof NonProto2) continue;
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, delegate, criteria));
        if (arePackagesRelated(aPackage, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, delegate, criteria));
          continue;
        }
      }
      descriptions.addAll(local(importedResource, delegate, criteria));
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> publicImported(Protobuf root, SearchDelegate delegate, Object criteria) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, delegate, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> local(Resource resource, SearchDelegate delegate, Object criteria) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      descriptions.addAll(delegate.descriptions(next, criteria));
      // TODO verify that call to 'importedNamesProvider.namesOf' is not necessary
    }
    return descriptions;
  }
}

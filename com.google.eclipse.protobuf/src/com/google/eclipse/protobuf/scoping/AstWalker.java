/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.*;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import java.util.*;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.parser.NonProto2;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class AstWalker {

  @Inject private ModelFinder modelFinder;
  @Inject private Imports imports;
  @Inject private Packages packages;
  @Inject private Resources resources;

  Collection<IEObjectDescription> traverseAst(EObject start, ScopeFinder scopeFinder, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject current = start.eContainer();
    while (current != null) {
      descriptions.addAll(local(current, scopeFinder, criteria));
      current = current.eContainer();
    }
    Protobuf root = modelFinder.rootOf(start);
    descriptions.addAll(imported(root, scopeFinder, criteria));
    return unmodifiableSet(descriptions);
  }

  Collection<IEObjectDescription> traverseAst(Protobuf start, ScopeFinder scopeFinder, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    descriptions.addAll(local(start, scopeFinder, criteria));
    descriptions.addAll(imported(start, scopeFinder, criteria));
    return unmodifiableSet(descriptions);
  }

  private Collection<IEObjectDescription> local(EObject start, ScopeFinder scopeFinder, Object criteria) {
    return local(start, scopeFinder, criteria, 0);
  }

  private Collection<IEObjectDescription> local(EObject start, ScopeFinder scopeFinder, Object criteria, int level) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (EObject element : start.eContents()) {
      descriptions.addAll(scopeFinder.local(element, criteria, level));
      if (element instanceof Message) {
        descriptions.addAll(local(element, scopeFinder, criteria, level + 1));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> imported(Protobuf start, ScopeFinder scopeFinder, Object criteria) {
    List<Import> allImports = modelFinder.importsIn(start);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelFinder.packageOf(start), resourceSet, scopeFinder, criteria);
  }

  private Collection<IEObjectDescription> imported(List<Import> allImports, Package fromImporter,
      ResourceSet resourceSet, ScopeFinder scopeFinder, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(scopeFinder.inDescriptor(anImport, criteria));
        continue;
      }
      Resource imported = resources.importedResource(anImport, resourceSet);
      Protobuf rootOfImported = modelFinder.rootOf(imported);
      if (rootOfImported instanceof NonProto2) continue;
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, scopeFinder, criteria));
        if (arePackagesRelated(fromImporter, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, scopeFinder, criteria));
          continue;
        }
        Package packageOfImported = modelFinder.packageOf(rootOfImported);
        descriptions.addAll(imported(fromImporter, packageOfImported, imported, scopeFinder, criteria));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> publicImported(Protobuf start, ScopeFinder scopeFinder, Object criteria) {
    if (start instanceof NonProto2) return emptySet();
    List<Import> allImports = modelFinder.publicImportsIn(start);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelFinder.packageOf(start), resourceSet, scopeFinder, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = modelFinder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported,
      Resource resource, ScopeFinder scopeFinder, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      descriptions.addAll(scopeFinder.imported(fromImporter, fromImported, next, criteria));
      // TODO verify that call to 'importedNamesProvider.namesOf' is not necessary
      
    }
    return descriptions;
  }
}

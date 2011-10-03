/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static java.util.Collections.*;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import com.google.eclipse.protobuf.model.OptionType;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionProperties {

  @Inject private Imports imports;
  @Inject private ModelFinder finder;
  @Inject private Options options;
  @Inject private Packages packages;
  @Inject private Resources resources;

  Collection<Property> propertiesFor(CustomOption option) {
    OptionType type = OptionType.typeOf(option);
    if (type == null) return emptyList();
    Protobuf root = finder.rootOf(option);
    if (root == null) return emptyList();
    List<Property> properties = new ArrayList<Property>();
    properties.addAll(local(root, type));
    properties.addAll(imported(root, type));
    return unmodifiableList(properties);
  }

  private Collection<Property> local(Protobuf root, OptionType type) {
    return local(root, type, 0);
  }

  private Collection<Property> local(EObject root, OptionType optionType, int level) {
    List<Property> properties = new ArrayList<Property>();
    for (EObject element : root.eContents()) {
      if (options.isExtendingOptionMessage(element, optionType)) {
        ExtendMessage extend = (ExtendMessage) element;
        for (MessageElement e : extend.getElements()) {
          if (!(e instanceof Property)) continue;
          properties.add((Property) e);
        }
      }
      if (element instanceof Message) {
        properties.addAll(local(element, optionType, level + 1));
      }
    }
    return unmodifiableList(properties);
  }

  private Collection<Property> imported(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private Collection<Property> imported(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, OptionType optionType) {
    List<Property> properties = new ArrayList<Property>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) continue;
      Resource importedResource = resources.importedResource(anImport, resourceSet);
      Protobuf importedRoot = finder.rootOf(importedResource);
      if (importedRoot != null) {
        properties.addAll(publicImported(importedRoot, optionType));
        if (arePackagesRelated(aPackage, importedRoot)) {
          properties.addAll(local(importedRoot, optionType));
          continue;
        }
      }
      properties.addAll(local(importedResource, optionType));
    }
    return unmodifiableList(properties);
  }

  private Collection<Property> publicImported(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<Property> local(Resource resource, OptionType optionType) {
    List<Property> properties = new ArrayList<Property>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!options.isExtendingOptionMessage((EObject) next, optionType)) continue;
      ExtendMessage extend = (ExtendMessage) next;
      for (MessageElement e : extend.getElements()) {
        if (e instanceof Property) properties.add((Property) e);
      }
    }
    return properties;
  }
}

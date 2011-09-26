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

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.*;
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
  @Inject private ProtobufElementFinder finder;
  @Inject private Options options;
  @Inject private Packages packages;
  @Inject private Resources resources;

  Collection<Property> propertiesFor(CustomOption option) {
    OptionType type = OptionType.typeOf(option);
    if (type == null) return emptyList();
    Protobuf root = finder.rootOf(option);
    if (root == null) return emptyList();
    List<Property> properties = new ArrayList<Property>();
    properties.addAll(localProperties(root, type));
    properties.addAll(importedProperties(root, type));
    return unmodifiableList(properties);
  }

  private Collection<Property> localProperties(Protobuf root, OptionType type) {
    return localProperties(root, type, 0);
  }

  private Collection<Property> localProperties(EObject root, OptionType optionType, int level) {
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
        properties.addAll(localProperties(element, optionType, level + 1));
      }
    }
    return unmodifiableList(properties);
  }

  private Collection<Property> importedProperties(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return importedProperties(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private Collection<Property> importedProperties(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, OptionType optionType) {
    List<Property> properties = new ArrayList<Property>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) continue;
      Resource importedResource = resources.importedResource(anImport, resourceSet);
      Protobuf importedRoot = finder.rootOf(importedResource);
      if (importedRoot != null) {
        properties.addAll(publicImportedProperties(importedRoot, optionType));
        if (arePackagesRelated(aPackage, importedRoot)) {
          properties.addAll(localProperties(importedRoot, optionType));
          continue;
        }
      }
      properties.addAll(localProperties(importedResource, optionType));
    }
    return unmodifiableList(properties);
  }

  private Collection<Property> publicImportedProperties(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return importedProperties(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<Property> localProperties(Resource resource, OptionType optionType) {
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

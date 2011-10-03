/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.model.OptionType.typeOf;
import static java.util.Collections.emptyList;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.OptionType;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionDescriptions {

  @Inject private ModelFinder finder;
  @Inject private ImportedNamesProvider importedNamesProvider;
  @Inject private Imports imports;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private Options options;
  @Inject private Packages packages;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;
  @Inject private Resources resources;

  Collection <IEObjectDescription> properties(EObject option) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    OptionType type = typeOf(option);
    EObject current = option.eContainer();
    while (current != null) {
      descriptions.addAll(local(current, type));
      current = current.eContainer();
    }
    Protobuf root = finder.rootOf(option);
    descriptions.addAll(imported(root, type));    
    return descriptions;
  }
  
  private Collection <IEObjectDescription> local(EObject root, OptionType optionType) {
    return local(root, optionType, 0);
  }

  private Collection <IEObjectDescription> local(EObject root, OptionType optionType, int level) {
    if (optionType == null) return emptyList();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (options.isExtendingOptionMessage(element, optionType)) {
        ExtendMessage extend = (ExtendMessage) element;
        for (MessageElement e : extend.getElements()) {
          if (!(e instanceof Property)) continue;
          Property p = (Property) e;
          List<QualifiedName> names = localNamesProvider.namesOf(p);
          int nameCount = names.size();
          for (int i = level; i < nameCount; i++) {
            descriptions.add(create(names.get(i), p));
          }
          descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(p));
        }
      }
      if (element instanceof Message) {
        descriptions.addAll(local(element, optionType, level + 1));
      }
    }
    return descriptions;
  }
  
  private Collection<IEObjectDescription> imported(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private Collection<IEObjectDescription> imported(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, OptionType optionType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) continue;
      Resource importedResource = resources.importedResource(anImport, resourceSet);
      Protobuf importedRoot = finder.rootOf(importedResource);
      if (importedRoot != null) {
        descriptions.addAll(publicImported(importedRoot, optionType));
        if (arePackagesRelated(aPackage, importedRoot)) {
          descriptions.addAll(local(importedRoot, optionType));
          continue;
        }
      }
      descriptions.addAll(local(importedResource, optionType));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> publicImported(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> local(Resource resource, OptionType optionType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!options.isExtendingOptionMessage((EObject) next, optionType)) continue;
      ExtendMessage extend = (ExtendMessage) next;
      for (MessageElement e : extend.getElements()) {
        if (!(e instanceof Property)) continue;
        Property p = (Property) e;
        descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(p));
        for (QualifiedName name : importedNamesProvider.namesOf(p)) {
          descriptions.add(create(name, e));
        }
      }
    }
    return descriptions;
  }

  /*
   * Scope for 'y' in:
   * option Type (x).y = 0;
   */
  Collection <IEObjectDescription> fields(Property optionProperty) {
    Message propertyType = finder.messageTypeOf(optionProperty);
    if (propertyType == null) return emptyList();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (MessageElement e : propertyType.getElements()) {
      if (!(e instanceof Property)) continue;
      Property optionPropertyField = (Property) e;
      descriptions.add(create(optionPropertyField.getName(), optionPropertyField));
    }
    return descriptions;
  }
}

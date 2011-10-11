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
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TypeDescriptions {

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private ModelFinder finder;
  @Inject private Imports imports;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private Packages packages;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;
  @Inject private Resources resources;

  Collection<IEObjectDescription> types(Property property) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject current = property.eContainer();
    Class<Type> targetType = Type.class;
    while (current != null) {
      descriptions.addAll(local(current, targetType));
      current = current.eContainer();
    }
    Protobuf root = finder.rootOf(property);
    descriptions.addAll(imported(root, targetType));
    return descriptions;
  }
  
  Collection<IEObjectDescription> messages(Protobuf root) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Class<Message> targetType = Message.class;
    descriptions.addAll(local(root, targetType));
    descriptions.addAll(imported(root, targetType));
    return descriptions;
  }
  
  private <T extends Type> Collection<IEObjectDescription> local(EObject root, Class<T> targetType) {
    return local(root, targetType, 0);
  }

  private <T extends Type> Collection<IEObjectDescription> local(EObject root, Class<T> targetType, int level) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (!targetType.isInstance(element)) continue;
      List<QualifiedName> names = localNamesProvider.namesOf(element);
      int nameCount = names.size();
      for (int i = level; i < nameCount; i++) {
        descriptions.add(create(names.get(i), element));
      }
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(element));
      // TODO investigate if groups can have messages, and if so, add those messages to the scope.
      if (element instanceof Message) {
        descriptions.addAll(local(element, targetType, level + 1));
      }
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> imported(Protobuf root, Class<T> targetType) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, targetType);
  }

  private <T extends Type> Collection<IEObjectDescription> imported(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(allNative(anImport, targetType));
        continue;
      }
      Resource importedResource = resources.importedResource(anImport, resourceSet);
      Protobuf rootOfImported = finder.rootOf(importedResource);
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, targetType));
        if (arePackagesRelated(aPackage, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, targetType));
          continue;
        }
      }
      descriptions.addAll(local(importedResource, targetType));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> allNative(Import anImport, Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    ProtoDescriptor descriptor = descriptorProvider.descriptor(anImport.getImportURI());
    for (Type t : descriptor.allTypes()) {
      if (!targetType.isInstance(t)) continue;
      T type = targetType.cast(t);
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> publicImported(Protobuf root, Class<T> targetType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return imported(allImports, finder.packageOf(root), resourceSet, targetType);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private <T extends Type> Collection<IEObjectDescription> local(Resource resource, Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!targetType.isInstance(next)) continue;
      T type = targetType.cast(next);
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type));
      // TODO verify that call to 'importedNamesProvider.namesOf' is not necessary
    }
    return descriptions;
  }
}

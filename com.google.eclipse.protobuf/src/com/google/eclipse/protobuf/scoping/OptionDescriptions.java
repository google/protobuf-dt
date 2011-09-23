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
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OptionDescriptions {

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private ProtobufElementFinder finder;
  @Inject private ImportedNamesProvider importedNamesProvider;
  @Inject private Imports imports;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private PackageResolver packageResolver;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;
  @Inject private ImportUriResolver uriResolver;

  Collection <IEObjectDescription> builtInOptionProperties(BuiltInOption option) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<Property> properties = descriptor.availableOptionPropertiesFor(option.eContainer());
    if (!properties.isEmpty()) return describe(properties);
    return emptyList();
  }

  Collection <IEObjectDescription> localCustomOptionProperties(EObject root, OptionType optionType) {
    return localCustomOptionProperties(root, optionType, 0);
  }

  private Collection <IEObjectDescription> localCustomOptionProperties(EObject root, OptionType optionType, int level) {
    if (optionType == null) return emptyList();
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (isExtendingOptionMessage(element, optionType)) {
        ExtendMessage extend = (ExtendMessage) element;
        for (MessageElement e : extend.getElements()) {
          if (!(e instanceof Property)) continue;
          List<QualifiedName> names = localNamesProvider.namesOf(e);
          int nameCount = names.size();
          for (int i = level; i < nameCount; i++) {
            descriptions.add(create(names.get(i), e));
          }
          descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e));
        }
        continue;
      }
      if (element instanceof Message) {
        descriptions.addAll(localCustomOptionProperties(element, optionType, level + 1));
      }
    }
    return descriptions;
  }

  Collection<IEObjectDescription> importedCustomOptionProperties(Protobuf root, OptionType optionType) {
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return importedCustomOptionProperties(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private Collection<IEObjectDescription> importedCustomOptionProperties(List<Import> allImports, Package aPackage,
      ResourceSet resourceSet, OptionType optionType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) continue;
      Resource importedResource = importedResource(anImport, resourceSet);
      Protobuf importedRoot = finder.rootOf(importedResource);
      if (importedRoot != null) {
        descriptions.addAll(publicImportedCustomOptionProperties(importedRoot, optionType));
        if (arePackagesRelated(aPackage, importedRoot)) {
          descriptions.addAll(localCustomOptionProperties(importedRoot, optionType));
          continue;
        }
      }
      descriptions.addAll(localCustomOptionProperties(importedResource, optionType));
    }
    return descriptions;
  }

  private Resource importedResource(Import anImport, ResourceSet resourceSet) {
    URI importUri = createURI(uriResolver.apply(anImport));
    try {
      return resourceSet.getResource(importUri, true);
    } catch (Throwable t) {
      return null;
    }
  }

  private <T extends Type> Collection<IEObjectDescription> publicImportedCustomOptionProperties(Protobuf root,
      OptionType optionType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    ResourceSet resourceSet = root.eResource().getResourceSet();
    return importedCustomOptionProperties(allImports, finder.packageOf(root), resourceSet, optionType);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packageResolver.areRelated(aPackage, p);
  }

  private Collection<IEObjectDescription> describe(Collection<Property> properties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : properties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> localCustomOptionProperties(Resource resource, OptionType optionType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!isExtendingOptionMessage(next, optionType)) continue;
      ExtendMessage extend = (ExtendMessage) next;
      for (MessageElement e : extend.getElements()) {
        if (!(e instanceof Property)) continue;
        descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e));
        for (QualifiedName name : importedNamesProvider.namesOf(e)) {
          descriptions.add(create(name, e));
        }
      }
    }
    return descriptions;
  }

  private boolean isExtendingOptionMessage(Object o, OptionType optionType) {
    if (!(o instanceof ExtendMessage)) return false;
    Message message = messageFrom((ExtendMessage) o);
    if (message == null) return false;
    return optionType.messageName.equals(message.getName());
  }

  private Message messageFrom(ExtendMessage extend) {
    MessageRef ref = extend.getMessage();
    return ref == null ? null : ref.getType();
  }
}

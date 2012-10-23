/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptySet;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.naming.LocalNamesProvider;
import com.google.eclipse.protobuf.naming.NormalNamingStrategy;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ComplexTypeFinderStrategy implements ModelElementFinder.FinderStrategy<Class<? extends ComplexType>> {
  @Inject private PackageIntersectionDescriptions packageIntersectionDescriptions;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private NormalNamingStrategy namingStrategy;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @Override public Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target,
      Class<? extends ComplexType> typeOfComplexType) {
    if (!typeOfComplexType.isInstance(target)) {
      return emptySet();
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    EObject e = (EObject) target;
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e, namingStrategy));
    descriptions.addAll(packageIntersectionDescriptions.intersection(fromImporter, fromImported, e));
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> inDescriptor(Import anImport,
      Class<? extends ComplexType> typeOfComplexType) {
    Set<IEObjectDescription> descriptions = newHashSet();
    ProtoDescriptor descriptor = descriptorProvider.descriptor(anImport.getImportURI());
    for (ComplexType complexType : descriptor.allTypes()) {
      if (!typeOfComplexType.isInstance(complexType)) {
        continue;
      }
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(complexType, namingStrategy));
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> local(Object target, Class<? extends ComplexType> typeOfComplexType,
      int level) {
   if (!typeOfComplexType.isInstance(target)) {
      return emptySet();
    }
    EObject e = (EObject) target;
    Set<IEObjectDescription> descriptions = newHashSet();
    List<QualifiedName> names = localNamesProvider.localNames(e, namingStrategy);
    int nameCount = names.size();
    for (int i = level; i < nameCount; i++) {
      descriptions.add(create(names.get(i), e));
    }
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e, namingStrategy));
    return descriptions;
  }
}

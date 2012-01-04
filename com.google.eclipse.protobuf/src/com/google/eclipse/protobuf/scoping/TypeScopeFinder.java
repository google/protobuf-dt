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

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.naming.LocalNamesProvider;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TypeScopeFinder implements ScopeFinder {
  @Inject private PackageIntersectionDescriptions packageIntersectionDescriptions;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @Override public Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target,
      Object criteria) {
    if (!isInstance(target, criteria)) {
      return emptySet();
    }
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject e = (EObject) target;
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e));
    descriptions.addAll(packageIntersectionDescriptions.intersection(fromImporter, fromImported, e));
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> inDescriptor(Import anImport, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    ProtoDescriptor descriptor = descriptorProvider.descriptor(anImport.getImportURI());
    for (ComplexType type : descriptor.allTypes()) {
      if (!isInstance(type, criteria)) {
        continue;
      }
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type));
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> local(Object target, Object criteria, int level) {
    if (!isInstance(target, criteria)) {
      return emptySet();
    }
    EObject e = (EObject) target;
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    List<QualifiedName> names = localNamesProvider.namesFor(e);
    int nameCount = names.size();
    for (int i = level; i < nameCount; i++) {
      descriptions.add(create(names.get(i), e));
    }
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e));
    return descriptions;
  }

  private boolean isInstance(Object target, Object criteria) {
    Class<?> targetType = targetTypeFrom(criteria);
    return targetType.isInstance(target);
  }

  private Class<?> targetTypeFrom(Object criteria) {
    if (criteria instanceof Class<?>) {
      return (Class<?>) criteria;
    }
    throw new IllegalArgumentException("Search criteria should be Class<?>");
  }
}

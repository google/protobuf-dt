/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.naming.Naming.NameTarget.TYPE;
import static java.util.Collections.emptySet;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.eclipse.protobuf.naming.Naming.NameTarget;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TypeScopeFinder implements ScopeFinder {

  private static final NameTarget NAME_TARGET = TYPE;
  
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @Override public Collection<IEObjectDescription> fromProtoDescriptor(Import anImport, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    ProtoDescriptor descriptor = descriptorProvider.descriptor(anImport.getImportURI());
    for (Type type : descriptor.allTypes()) {
      if (!isInstance(type, criteria)) continue;
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type, NAME_TARGET));
    }
    return descriptions;
  }
  
  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria) {
    if (!isInstance(target, criteria)) return emptySet();
    EObject e = (EObject) target;
    return qualifiedNamesDescriptions.qualifiedNames(e, NAME_TARGET);
  }

  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria, int level) {
    if (!isInstance(target, criteria)) return emptySet();
    EObject e = (EObject) target;
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    List<QualifiedName> names = localNamesProvider.namesOf(e, NAME_TARGET);
    int nameCount = names.size();
    for (int i = level; i < nameCount; i++) {
      descriptions.add(create(names.get(i), e));
    }
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e, NAME_TARGET));
    return descriptions;
  }
  
  private boolean isInstance(Object target, Object criteria) {
    Class<?>[] targetTypes = targetTypesFrom(criteria);
    for (Class<?> type : targetTypes) {
      if (type.isInstance(target)) return true;
    }
    return false;
  }
  
  private Class<?>[] targetTypesFrom(Object criteria) {
    if (criteria instanceof Class<?>[]) return (Class<?>[]) criteria;
    throw new IllegalArgumentException("Search criteria should be an array of Class<? extends EObject>");
  }
}

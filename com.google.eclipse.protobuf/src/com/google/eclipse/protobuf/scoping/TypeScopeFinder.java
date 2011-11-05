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

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TypeScopeFinder implements ScopeFinder {

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @Override public Collection<IEObjectDescription> fromProtoDescriptor(Import anImport, Object criteria) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Class<? extends Type> targetType = targetTypeFrom(criteria);
    ProtoDescriptor descriptor = descriptorProvider.descriptor(anImport.getImportURI());
    for (Type type : descriptor.allTypes()) {
      if (!targetType.isInstance(type)) continue;
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type));
    }
    return descriptions;
  }
  
  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria) {
    Class<? extends Type> targetType = targetTypeFrom(criteria);
    if (!targetType.isInstance(target)) return emptySet();
    Type type = targetType.cast(target);
    return qualifiedNamesDescriptions.qualifiedNames(type);
  }

  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria, int level) {
    Class<? extends Type> targetType = targetTypeFrom(criteria);
    if (!targetType.isInstance(target)) return emptySet();
    Type type = targetType.cast(target);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    List<QualifiedName> names = localNamesProvider.namesOf(type);
    int nameCount = names.size();
    for (int i = level; i < nameCount; i++) {
      descriptions.add(create(names.get(i), type));
    }
    descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(type));
    return descriptions;
  }
  
  @SuppressWarnings("unchecked") 
  private Class<? extends Type> targetTypeFrom(Object criteria) {
    if (!(criteria instanceof Class)) 
      throw new IllegalArgumentException("Search criteria should be Class<? extends Type>");
    return (Class<? extends Type>) criteria;
  }
}

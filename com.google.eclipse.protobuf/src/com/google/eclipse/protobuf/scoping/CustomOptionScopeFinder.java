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

import com.google.eclipse.protobuf.model.util.ModelFinder;
import com.google.eclipse.protobuf.naming.LocalNamesProvider;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionScopeFinder implements ScopeFinder {

  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private ModelFinder modelFinder;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @Override public Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target,
      Object criteria) {
    OptionType optionType = optionTypeFrom(criteria);
    if (!isExtendingOptionMessage(target, optionType)) return emptySet();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    TypeExtension extension = (TypeExtension) target;
    for (MessageElement e : extension.getElements()) {
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNamesForOption(e));
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> inDescriptor(Import anImport, Object criteria) {
    return emptySet();
  }

  @Override public Collection<IEObjectDescription> local(Object target, Object criteria, int level) {
    OptionType optionType = optionTypeFrom(criteria);
    if (!isExtendingOptionMessage(target, optionType)) return emptySet();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    TypeExtension extension = (TypeExtension) target;
    for (MessageElement e : extension.getElements()) {
      List<QualifiedName> names = localNamesProvider.namesForOption(e);
      int nameCount = names.size();
      for (int i = level; i < nameCount; i++) {
        descriptions.add(create(names.get(i), e));
      }
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNamesForOption(e));
    }
    return descriptions;
  }

  private OptionType optionTypeFrom(Object criteria) {
    if (!(criteria instanceof OptionType))
      throw new IllegalArgumentException("Search criteria should be OptionType");
    return (OptionType) criteria;
  }

  private boolean isExtendingOptionMessage(Object o, OptionType optionType) {
    if (!(o instanceof TypeExtension)) return false;
    Message message = modelFinder.messageFrom((TypeExtension) o);
    if (message == null) return false;
    String name = message.getName();
    return optionType.messageName().equals(name);
  }
}

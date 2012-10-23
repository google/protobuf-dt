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

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.TypeExtensions;
import com.google.eclipse.protobuf.naming.LocalNamesProvider;
import com.google.eclipse.protobuf.naming.OptionNamingStrategy;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionFinderStrategy implements ModelElementFinder.FinderStrategy<OptionType> {
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private OptionNamingStrategy namingStrategy;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;
  @Inject private TypeExtensions typeExtensions;

  @Override public Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target,
      OptionType optionType) {
    if (!isExtendingOptionMessage(target, optionType)) {
      return emptySet();
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    TypeExtension extension = (TypeExtension) target;
    for (MessageElement e : extension.getElements()) {
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e, namingStrategy));
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> inDescriptor(Import anImport, OptionType criteria) {
    return emptySet();
  }

  @Override public Collection<IEObjectDescription> local(Object target, OptionType optionType, int level) {
    if (!isExtendingOptionMessage(target, optionType)) {
      return emptySet();
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    TypeExtension extension = (TypeExtension) target;
    for (MessageElement e : extension.getElements()) {
      List<QualifiedName> names = localNamesProvider.localNames(e, namingStrategy);
      int nameCount = names.size();
      for (int i = level; i < nameCount; i++) {
        descriptions.add(create(names.get(i), e));
      }
      descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(e, namingStrategy));
    }
    return descriptions;
  }

  private boolean isExtendingOptionMessage(Object o, OptionType optionType) {
    if (!(o instanceof TypeExtension)) {
      return false;
    }
    Message message = typeExtensions.messageFrom((TypeExtension) o);
    if (message == null) {
      return false;
    }
    String name = message.getName();
    return optionType.messageName().equals(name);
  }
}

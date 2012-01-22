/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;

import java.util.List;

/**
 * Provides fully-qualified names for protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl implements
    IProtobufQualifiedNameProvider {
  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  @Inject private ModelObjects modelObjects;
  @Inject private NamingStrategies namingStrategies;
  @Inject private Packages packages;
  @Inject private QualifiedNames qualifiedNames;

  @Override public QualifiedName getFullyQualifiedName(EObject target) {
    return getFullyQualifiedName(target, namingStrategies.normal());
  }

  @Override public QualifiedName getFullyQualifiedNameForOption(EObject source) {
    return getFullyQualifiedName(source, namingStrategies.option());
  }

  private QualifiedName getFullyQualifiedName(final EObject e, final NamingStrategy naming) {
    EObject current = e;
    String name = naming.nameOf(e);
    if (isEmpty(name)) {
      return null;
    }
    QualifiedName qualifiedName = converter.toQualifiedName(name);
    while (current.eContainer() != null) {
      current = current.eContainer();
      QualifiedName parentsQualifiedName = getFullyQualifiedName(current, naming);
      if (parentsQualifiedName != null) {
        return parentsQualifiedName.append(qualifiedName);
      }
    }
    return addPackage(e, qualifiedName);
  }

  private QualifiedName addPackage(EObject obj, QualifiedName qualifiedName) {
    if (qualifiedName == null || obj instanceof Package) {
      return qualifiedName;
    }
    Package p = modelObjects.packageOf(obj);
    if (p == null) {
      return qualifiedName;
    }
    List<String> segments = packages.segmentsOf(p);
    if (segments.isEmpty()) {
      return qualifiedName;
    }
    QualifiedName packageQualifiedName = qualifiedNames.createFqn(segments);
    if (qualifiedName.startsWith(packageQualifiedName)) {
      return qualifiedName;
    }
    return packageQualifiedName.append(qualifiedName);
  }
}

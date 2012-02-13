/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.naming.NameType.NORMAL;
import static org.eclipse.xtext.util.Tuples.pair;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.*;

import java.util.List;

/**
 * Provides fully-qualified names for protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl implements
    IProtobufQualifiedNameProvider {
  private static final Pair<NameType, QualifiedName> EMPTY_NAME = pair(NORMAL, null);

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  @Inject private ModelObjects modelObjects;
  @Inject private NormalNamingStrategy normalNamingStrategy;
  @Inject private Packages packages;
  @Inject private QualifiedNames qualifiedNames;

  @Override public QualifiedName getFullyQualifiedName(EObject target) {
    return getFullyQualifiedName(target, normalNamingStrategy);
  }

  @Override public QualifiedName getFullyQualifiedName(final EObject e, final NamingStrategy namingStrategy) {
    Pair<EObject, String> key = pair(e, "fqn");
    Pair<NameType, QualifiedName> cached = cache.get(key, e.eResource(), new Provider<Pair<NameType, QualifiedName>>() {
      @Override public Pair<NameType, QualifiedName> get() {
        EObject current = e;
        Pair<NameType, String> name = namingStrategy.nameOf(e);
        if (name == null) {
          return EMPTY_NAME;
        }
        QualifiedName qualifiedName = converter.toQualifiedName(name.getSecond());
        while (current.eContainer() != null) {
          current = current.eContainer();
          QualifiedName parentsQualifiedName = getFullyQualifiedName(current, namingStrategy);
          if (parentsQualifiedName != null) {
            return pair(name.getFirst(), parentsQualifiedName.append(qualifiedName));
          }
        }
        return pair(name.getFirst(), addPackage(e, qualifiedName));
      }
    });
    return cached.getSecond();
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
    QualifiedName packageQualifiedName = qualifiedNames.createQualifiedName(segments);
    if (qualifiedName.startsWith(packageQualifiedName)) {
      return qualifiedName;
    }
    return packageQualifiedName.append(qualifiedName);
  }
}

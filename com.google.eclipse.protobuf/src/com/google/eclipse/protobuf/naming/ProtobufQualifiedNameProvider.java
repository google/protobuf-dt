/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.eclipse.protobuf.naming.Naming.NamingUsage.*;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.naming.Naming.NamingUsage;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.*;

/**
 * Provides fully-qualified names for protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl 
    implements IProtobufQualifiedNameProvider {

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  @Inject private ModelFinder finder;
  @Inject private Naming naming;

  @Override public QualifiedName getFullyQualifiedName(EObject e) {
    return getFullyQualifiedName(e, DEFAULT);
  }

  @Override public QualifiedName getFullyQualifiedNameForOption(EObject e) {
    return getFullyQualifiedName(e, OPTION);
  }
  
  private QualifiedName getFullyQualifiedName(final EObject e, final NamingUsage usage) {
    Pair<EObject, String> key = pair(e, "fqn");
    return cache.get(key, e.eResource(), new Provider<QualifiedName>() {
      @Override public QualifiedName get() {
        EObject current = e;
        String name = naming.nameOf(e, usage);
        if (isEmpty(name)) return null;
        QualifiedName qualifiedName = converter.toQualifiedName(name);
        while (current.eContainer() != null) {
          current = current.eContainer();
          QualifiedName parentsQualifiedName = getFullyQualifiedName(current, usage);
          if (parentsQualifiedName != null) {
            return parentsQualifiedName.append(qualifiedName);
          }
        }
        return addPackage(e, qualifiedName);
      }
    });
  }

  private QualifiedName addPackage(EObject obj, QualifiedName qualifiedName) {
    if (qualifiedName == null || obj instanceof Package)
      return qualifiedName;
    Package p = finder.packageOf(obj);
    if (p == null) return qualifiedName;
    String packageName = p.getName();
    if (isEmpty(packageName)) return qualifiedName;
    QualifiedName packageQualifiedName = converter.toQualifiedName(packageName);
    if (qualifiedName.startsWith(packageQualifiedName)) return qualifiedName;
    return packageQualifiedName.append(qualifiedName);
  }
}

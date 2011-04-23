/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.util.Pair;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.EObjectFinder;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides fully-qualified names for protobuf elements.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl {

  @Inject private IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
  @Inject private IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  @Inject private EObjectFinder finder;
  
  private Function<EObject, String> resolver = newResolver(String.class, "name");

  public QualifiedName getFullyQualifiedName(final EObject obj) {
    Pair<EObject, String> key = pair(obj, "fqn");
    return cache.get(key, obj.eResource(), new Provider<QualifiedName>() {
      public QualifiedName get() {
        EObject current = obj;
        String name = resolver.apply(current);
        if (isEmpty(name)) return null;
        QualifiedName qualifiedName = converter.toQualifiedName(name);
        while (current.eContainer() != null) {
          current = current.eContainer();
          QualifiedName parentsQualifiedName = getFullyQualifiedName(current);
          if (parentsQualifiedName != null)
            return parentsQualifiedName.append(qualifiedName);
        }
        return addPackage(obj, qualifiedName);
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

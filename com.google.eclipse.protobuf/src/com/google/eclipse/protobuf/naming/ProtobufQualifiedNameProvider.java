/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.Tuples.pair;

import static com.google.eclipse.protobuf.naming.NameType.NORMAL;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.util.Pair;

import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.model.util.QualifiedNames;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.BooleanLink;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.NumberLink;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ScalarTypeLink;
import com.google.eclipse.protobuf.protobuf.StringLink;
import com.google.eclipse.protobuf.protobuf.ValueField;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides fully-qualified names for protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl implements
    IProtobufQualifiedNameProvider {
  private static final Pair<NameType, QualifiedName> EMPTY_NAME = pair(NORMAL, null);

  private static final Class<?>[] IGNORED_TYPES = { Protobuf.class, Import.class, AbstractOption.class,
    OptionSource.class, ScalarTypeLink.class, NumberLink.class, BooleanLink.class, StringLink.class, ComplexValue.class,
    ValueField.class, FieldName.class };

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
    if (shouldIgnore(e)) {
      return null;
    }
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

  private boolean shouldIgnore(EObject e) {
    for (Class<?> ignoredType : IGNORED_TYPES) {
      if (ignoredType.isInstance(e)) {
        return true;
      }
    }
    return false;
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

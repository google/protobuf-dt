/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static java.util.Collections.emptyList;

import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.naming.NameType.NORMAL;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.util.Pair;

import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides alternative qualified names for protobuf elements.
 * <p>
 * For example, given the following proto element:
 *
 * <pre>
 * package test.alternative.names;
 *
 * message Person {
 *   optional string name = 1;
 *
 *   enum PhoneType {
 *     HOME = 0;
 *     WORK = 1;
 *   }
 * }
 * </pre>
 *
 * The default qualified name for {@code PhoneType} is {@code alternative.names.Person.PhoneType}. The problem is that
 * protoc also recognizes the following as qualified names:
 * <ul>
 * <li>{@code PhoneType}</li>
 * <li>{@code Person.PhoneType}</li>
 * <li>{@code names.Person.PhoneType}</li>
 * <li>{@code test.names.Person.PhoneType}</li>
 * </ul>
 * </p>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LocalNamesProvider {
  private static Pair<NameType, List<QualifiedName>> EMPTY_NAMES = emptyNames();

  private static Pair<NameType, List<QualifiedName>> emptyNames() {
    List<QualifiedName> names = emptyList();
    return pair(NORMAL, names);
  }

  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  @Inject private ModelObjects modelObjects;
  @Inject private NameResolver nameResolver;
  @Inject private IQualifiedNameConverter qualifiedNameConverter;
  @Inject private Packages packages;

  public List<QualifiedName> localNames(final EObject e, final NamingStrategy strategy) {
    Pair<EObject, String> key = pair(e, "localFqns");
    Pair<NameType, List<QualifiedName>> cached = cache.get(key, e.eResource(),
        new Provider<Pair<NameType, List<QualifiedName>>>() {
      @Override public Pair<NameType, List<QualifiedName>> get() {
        List<QualifiedName> allNames = newArrayList();
        EObject current = e;
        Pair<NameType, String> name = strategy.nameOf(e);
        if (name == null) {
          return EMPTY_NAMES;
        }
        QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(name.getSecond());
        allNames.add(qualifiedName);
        while (current.eContainer() != null) {
          current = current.eContainer();
          String containerName = nameResolver.nameOf(current);
          if (isEmpty(containerName)) {
            continue;
          }
          qualifiedName = qualifiedNameConverter.toQualifiedName(containerName).append(qualifiedName);
          allNames.add(qualifiedName);
        }
        allNames.addAll(packages.addPackageNameSegments(modelObjects.packageOf(e), qualifiedName));
        return pair(name.getFirst(), allNames);
      }
    });
    return cached.getSecond();
  }
}

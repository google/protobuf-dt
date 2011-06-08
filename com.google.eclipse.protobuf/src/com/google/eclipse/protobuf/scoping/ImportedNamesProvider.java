/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.QualifiedNames.addPackageNameSegments;
import static java.util.Collections.*;
import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.util.ProtobufElementFinder;
import com.google.inject.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.*;

import java.util.*;

/**
 * Provides alternative qualified names for imported protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportedNamesProvider {

  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;
  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  @Inject private ProtobufElementFinder finder;

  private final Function<EObject, String> resolver = newResolver(String.class, "name");

  List<QualifiedName> namesOf(final EObject obj) {
    Pair<EObject, String> key = pair(obj, "importedFqns");
    return cache.get(key, obj.eResource(), new Provider<List<QualifiedName>>() {
      public List<QualifiedName> get() {
        List<QualifiedName> allNames = new ArrayList<QualifiedName>();
        EObject current = obj;
        String name = resolver.apply(current);
        if (isEmpty(name)) return emptyList();
        QualifiedName qualifiedName = converter.toQualifiedName(name);
        allNames.add(qualifiedName);
        allNames.addAll(addPackageNameSegments(qualifiedName, finder.packageOf(obj), converter));
        return unmodifiableList(allNames);
      }
    });
  }
}

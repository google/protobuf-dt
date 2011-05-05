/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.unmodifiableList;
import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.*;

import com.google.common.base.Function;
import com.google.inject.*;

/**
 * Provides alternative qualified names for protobuf elements.
 * <p>
 * For example, given the following proto element:
 * <pre>
 * package alternative.names;
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
 * The default qualified name for {@code PhoneType} is {@code alternative.names.Person.PhoneType}. The problem is that
 * protoc also recognizes the following as qualified names:
 * <ul>
 * <li>{@code PhoneType}</li>
 * <li>{@code Person.PhoneType}</li>
 * </ul>
 * </p>
 * <p>
 * This class provides the non-default qualified names recognized by protoc.
 * </p>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class AlternativeQualifiedNamesProvider {

  // TODO: in example above, it looks like protoc considers names.Person.PhoneType as valid. In short, qualified
  // names can use segments of the package as well.

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  private final Function<EObject, String> resolver = newResolver(String.class, "name");

  List<QualifiedName> alternativeFullyQualifiedNames(final EObject obj) {
    Pair<EObject, String> key = pair(obj, "fqns");
    return cache.get(key, obj.eResource(), new Provider<List<QualifiedName>>() {
      public List<QualifiedName> get() {
        List<QualifiedName> names = new ArrayList<QualifiedName>();
        EObject current = obj;
        String name = resolver.apply(current);
        if (isEmpty(name)) return names;
        QualifiedName qualifiedName = converter.toQualifiedName(name);
        names.add(qualifiedName);
        while (current.eContainer() != null) {
          current = current.eContainer();
          String containerName = resolver.apply(current);
          if (isEmpty(containerName)) continue;
          qualifiedName = converter.toQualifiedName(containerName).append(qualifiedName);
          names.add(qualifiedName);
        }
        return unmodifiableList(names);
      }
    });
  }
}

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
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
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
  private static final Class<?>[] IGNORED_TYPES = { Protobuf.class, Package.class, Import.class, Option.class, SimpleValueLink.class};

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
//    final long start = System.currentTimeMillis();
//    final AtomicLong end = new AtomicLong();
//    final AtomicBoolean isCached = new AtomicBoolean(true);
    for (Class<?> ignored : IGNORED_TYPES) {
      if (ignored.isInstance(e)) {
        return null;
      }
    }
    Pair<EObject, String> key = pair(e, "fqn");
    Resource resource = e.eResource();
    URI uri = resource.getURI();
//    System.out.println(uri);
    Pair<NameType, QualifiedName> cached = cache.get(key, resource, new Provider<Pair<NameType, QualifiedName>>() {
      @Override public Pair<NameType, QualifiedName> get() {
//        isCached.set(false);
        EObject current = e;
        Pair<NameType, String> name = namingStrategy.nameOf(e);
        if (name == null) {
//          end.set(System.currentTimeMillis());
          return EMPTY_NAME;
        }
        QualifiedName qualifiedName = converter.toQualifiedName(name.getSecond());
        while (current.eContainer() != null) {
          current = current.eContainer();
          QualifiedName parentsQualifiedName = getFullyQualifiedName(current, namingStrategy);
          if (parentsQualifiedName != null) {
//            end.set(System.currentTimeMillis());
            return pair(name.getFirst(), parentsQualifiedName.append(qualifiedName));
          }
        }
//        end.set(System.currentTimeMillis());
        return pair(name.getFirst(), addPackage(e, qualifiedName));
      }
    });
    QualifiedName qualifiedName = cached.getSecond();
//    if (isCached.get()) {
//      end.set(System.currentTimeMillis());
//    }
//    double seconds = (end.get() - start) / 1000;
//    System.out.println("URI: " + uri.toString() + ", type: " + e.getClass().getSimpleName() + ", qualified name: " + qualifiedName + ", cached? " + isCached.get() + ", time: " + seconds);
    return qualifiedName;
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

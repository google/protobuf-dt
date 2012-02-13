/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.eclipse.protobuf.cdt.matching.ContentsByType.contentsOf;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE;
import static java.util.Collections.emptyList;

import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.StringLists;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementMatchFinder {
  private final Map<EClass, ProtobufElementMatcherStrategy> strategies = newHashMap();

  @Inject private Packages packages;
  @Inject private Resources resources;
  @Inject private StringLists stringLists;

  @Inject public ProtobufElementMatchFinder(MessageMatcherStrategy s1) {
    register(s1);
  }

  private void register(MessageMatcherStrategy strategy) {
    strategies.put(strategy.supportedType(), strategy);
  }

  public List<URI> matchingProtobufElementLocations(Resource resource, CppToProtobufMapping mapping) {
    Protobuf root = resources.rootOf(resource);
    // TODO check for proto2?
    ContentsByType contents = contentsOf(root);
    String[] qualifiedNameSegments = removePackageFromQualifiedName(mapping.qualifiedNameSegments(), contents);
    ProtobufElementMatcherStrategy strategy = strategies.get(mapping.type());
    if (strategy != null) {
      return strategy.matchingProtobufElementLocations(contents, qualifiedNameSegments);
    }
    return emptyList();
  }

  private String[] removePackageFromQualifiedName(List<String> qualifiedNameSegments, ContentsByType contents) {
    Package aPackage = packageFrom(contents);
    if (aPackage != null) {
      return stringLists.toArray(qualifiedNameSegments);
    }
    List<String> segments = newArrayList(qualifiedNameSegments);
    for (String packageSegment : packages.segmentsOf(aPackage)) {
      if (segments.isEmpty() || !packageSegment.equals(segments.get(0))) {
        break;
      }
      segments.remove(0);
    }
    return stringLists.toArray(segments);
  }

  private Package packageFrom(ContentsByType contents) {
    List<EObject> packages = contents.ofType(PACKAGE);
    if (packages.isEmpty()) {
      return null;
    }
    return (Package) packages.get(0);
  }
}

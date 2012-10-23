/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static java.util.Collections.emptyList;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.eclipse.protobuf.cdt.util.ExtendedListIterator.newIterator;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementMatchFinder {
  private final Map<EClass, AbstractProtobufElementMatcherStrategy> strategies = newHashMap();

  @Inject private Resources resources;

  @Inject public ProtobufElementMatchFinder(MessageMatcherStrategy s1) {
    register(s1);
  }

  private void register(MessageMatcherStrategy strategy) {
    strategies.put(strategy.supportedType(), strategy);
  }

  public List<URI> matchingProtobufElementLocations(Resource resource, CppToProtobufMapping mapping) {
    Protobuf root = resources.rootOf(resource);
    // TODO check for proto2?
    List<String> qualifiedName = mapping.qualifiedName();
    AbstractProtobufElementMatcherStrategy strategy = strategies.get(mapping.type());
    if (strategy != null) {
      return strategy.matchingProtobufElementLocations(root, newIterator(qualifiedName));
    }
    return emptyList();
  }

}

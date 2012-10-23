/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.cdt.util.ExtendedIterator;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
abstract class AbstractProtobufElementMatcherStrategy {
  static final String NESTED_ELEMENT_SEPARATOR = "_";

  abstract List<URI> matchingProtobufElementLocations(EObject root, ExtendedIterator<String> qualifiedNameSegments);

  boolean isSupported(EObject o) {
    return supportedType().equals(o.eClass());
  }

  abstract EClass supportedType();
}

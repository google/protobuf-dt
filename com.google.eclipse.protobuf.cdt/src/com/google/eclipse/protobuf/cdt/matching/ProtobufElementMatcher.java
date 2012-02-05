/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.IResourceDescription;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementMatcher {
  @Inject private ComplexTypeMatcherStrategy complexTypeMatcherStrategy;

  /**
   * Returns the URI of the protocol buffer element in the given resource, whose qualified name matches the one in the
   * given <code>{@link CppToProtobufMapping}</code>.
   * @param resource describes the contents of a .proto file.
   * @param mapping information of the protocol buffer element to look for.
   * @return the found URI, or {@code null} if it was not possible to find a matching protocol buffer element.
   */
  public URI findUriOfMatchingProtobufElement(IResourceDescription resource, CppToProtobufMapping mapping) {
    if (complexTypeMatcherStrategy.canHandle(mapping.type())) {
      return complexTypeMatcherStrategy.findUriOfMatchingProtobufElement(resource, mapping);
    }
    return null;
  }
}

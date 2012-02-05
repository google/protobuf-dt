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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IResourceDescription;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
interface ProtobufElementMatcherStrategy {
  URI findUriOfMatchingProtobufElement(IResourceDescription resource, CppToProtobufMapping mapping);

  boolean canHandle(Class<? extends EObject> protobufElementType);
}

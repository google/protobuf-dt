/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import org.eclipse.core.runtime.CoreException;

import com.google.inject.ImplementedBy;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(CompositeOutputParser.class)
interface ProtocOutputParser {
  boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException;
}

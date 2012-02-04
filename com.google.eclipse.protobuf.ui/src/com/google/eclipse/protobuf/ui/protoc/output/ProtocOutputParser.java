/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import org.eclipse.core.runtime.CoreException;

import com.google.inject.ImplementedBy;

/**
 * Parser of protoc output.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(CompositeOutputParser.class)
public interface ProtocOutputParser {
  /**
   * Parses a single line of protoc output. It may create an editor marker.
   * @param line the line to process.
   * @param markerFactory the factory of editor markers.
   * @return {@code true} if the given line was parsed, {@code false} otherwise.
   * @throws CoreException if something wrong happens.
   */
  boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException;
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.protobuf.Import;

/**
 * Fixes partial URIs in <code>{@link Import}</code>s by resolving them to existing files with matching URIs.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface ImportUriFixer {

  /**
   * Prefix present in complete import URIs.
   */
  String PREFIX = "platform:/resource";
  
  /**
   * Returns a "fixed" full URI based on the given partial URI.
   * @param importUri the partial URI (comes from a {@code Import}.)
   * @param resourceUri the URI of the resource declaring the import.
   * @param checker indicates whether the resolved URI belongs to an existing file.
   * @return the "fixed" full URI.
   */
  String fixUri(String importUri, URI resourceUri, ResourceChecker checker);
}
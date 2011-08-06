/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Singleton;

/**
 * Utility methods related to imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Imports {

  /**
   * Indicates whether the URI of the given import is equal to the path of descriptor.proto
   * ("google/protobuf/descriptor.proto").
   * @param anImport the import to check.
   * @return {@code true} if the URI of the given import is equal to the path of descriptor.proto, {@code false}
   * otherwise.
   */
  public boolean hasUnresolvedDescriptorUri(Import anImport) {
    if (anImport == null) return false;
    return isUnresolvedDescriptorUri(anImport.getImportURI());
  }

  /**
   * Indicates whether the given import URI is equal to the path of descriptor.proto
   * ("google/protobuf/descriptor.proto").
   * @param uri the URI to check.
   * @return {@code true} if the given import URI is equal to the path of descriptor.proto, {@code false} otherwise.
   */
  public boolean isUnresolvedDescriptorUri(String uri) {
    return "google/protobuf/descriptor.proto".equals(uri);
  }
}

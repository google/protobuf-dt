/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.inject.*;

/**
 * Utility methods related to imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Imports {
  @Inject private ProtoDescriptorProvider descriptorProvider;

  /**
   * Indicates whether the URI of the given import is equal to the path of descriptor.proto
   * ("google/protobuf/descriptor.proto").
   * @param anImport the import to check.
   * @return {@code true} if the URI of the given import is equal to the path of descriptor.proto, {@code false}
   * otherwise.
   */
  public boolean hasUnresolvedDescriptorUri(Import anImport) {
    if (anImport == null) {
      return false;
    }
    return descriptorProvider.descriptorLocation(anImport.getImportURI()) != null;
  }

  /**
   * Indicates whether the given <code>{@link Import}</code> is pointing to descriptor.proto.
   * @param anImport the given import to check.
   * @return {@code true} if the given import is pointing to descriptor.proto, {@code false} otherwise.
   */
  public boolean isImportingDescriptor(Import anImport) {
    if (hasUnresolvedDescriptorUri(anImport)) {
      return true;
    }
    if (anImport == null) {
      return false;
    }
    String importUri = anImport.getImportURI();
    for (URI locationUri : descriptorProvider.allDescriptorLocations()) {
      String location = locationUri.toString();
      if (location.equals(importUri)) {
        return true;
      }
    }
    return false;
  }
}

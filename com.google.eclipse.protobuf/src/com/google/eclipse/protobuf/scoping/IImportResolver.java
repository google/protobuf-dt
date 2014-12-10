/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IImportResolver.NullImportResolver;
import com.google.inject.ImplementedBy;

/**
 * Resolves "import" URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(NullImportResolver.class)
public interface IImportResolver {
  /**
   * Returns the resolved path of the given {@code Import} or {@code null} if the path cannot be
   * resolved.
   *
   * @param anImport the given {@code Import}.
   */
  String resolve(Import anImport);

  /**
   * Invalidates any cached results for the resolution of the given import.
   */
  void invalidateCacheFor(Import anImport);

  class NullImportResolver implements IImportResolver {
    @Override public String resolve(Import anImport) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateCacheFor(Import anImport) {
      throw new UnsupportedOperationException();
    }
  }
}
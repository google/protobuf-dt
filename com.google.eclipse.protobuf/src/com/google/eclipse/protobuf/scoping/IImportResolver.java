/*
 * Copyright (c) 2011, 2014 Google Inc.
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
   * Resolves the URI of the given {@code Import}. This method will update the URI of the given {@code Import} if it was
   * successfully resolved.
   * @param anImport the given {@code Import}.
   */
  void resolveAndUpdateUri(Import anImport);

  class NullImportResolver implements IImportResolver {
    @Override public void resolveAndUpdateUri(Import anImport) {
      throw new UnsupportedOperationException();
    }
  }
}
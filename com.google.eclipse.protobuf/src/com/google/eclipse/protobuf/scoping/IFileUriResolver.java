/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IFileUriResolver.NullFileUriResolver;
import com.google.inject.ImplementedBy;

/**
 * Resolves "import" URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(NullFileUriResolver.class)
public interface IFileUriResolver {
  /**
   * Resolves the URI of the given {@code Import}.
   * @param anImport the given {@code Import}.
   * @return the resolved URI, or {@code null} if resolution was either not necessary or not successful.
   */
  String resolveUri(Import anImport);

  class NullFileUriResolver implements IFileUriResolver {
    @Override public String resolveUri(Import anImport) {
      throw new UnsupportedOperationException();
    }
  }
}
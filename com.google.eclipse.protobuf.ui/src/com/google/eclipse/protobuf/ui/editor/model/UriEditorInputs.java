/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import java.io.File;
import java.net.URI;

import org.eclipse.ui.IURIEditorInput;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class UriEditorInputs {
  private static final String URI_SCHEME_FOR_FILES = "file";

  File fileFrom(IURIEditorInput input) {
    URI uri = input.getURI();
    String scheme = uri.getScheme();
    if (scheme != URI_SCHEME_FOR_FILES) {
      String cleanUri = uri.toString().replaceFirst(scheme, URI_SCHEME_FOR_FILES);
      uri = URI.create(cleanUri);
    }
    return new File(uri);
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.resource.IResourceVerifier;
import com.google.eclipse.protobuf.ui.preferences.editor.ignore.IgnoredExtensionsPreferences;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceVerifier implements IResourceVerifier {
  private final String[] extensions;

  @Inject public ResourceVerifier(IPreferenceStoreAccess storeAccess) {
    IgnoredExtensionsPreferences preferences = new IgnoredExtensionsPreferences(storeAccess);
    extensions = preferences.extensions();
  }

  @Override public boolean shouldIgnore(URI uri) {
    String fileName = uri.lastSegment();
    for (String extension : extensions) {
      if (fileName.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }
}

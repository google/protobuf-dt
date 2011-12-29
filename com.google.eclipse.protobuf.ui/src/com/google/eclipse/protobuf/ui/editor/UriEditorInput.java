/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static com.google.eclipse.protobuf.util.Objects.*;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class UriEditorInput implements IEditorInput {
  private final URI fileUri;
  private final String name;

  public UriEditorInput(URI fileUri) {
    this.fileUri = fileUri.trimFragment();
    name = this.fileUri.segment(this.fileUri.segmentCount() - 1);
  }

  @SuppressWarnings("rawtypes")
  @Override public Object getAdapter(Class adapter) {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  @Override public boolean exists() {
    return false;
  }

  @Override public ImageDescriptor getImageDescriptor() {
    return null;
  }

  @Override public String getName() {
    return name;
  }

  @Override public IPersistableElement getPersistable() {
    return null;
  }

  @Override public String getToolTipText() {
    return name;
  }

  public URI getFileUri() {
    return fileUri;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    UriEditorInput other = (UriEditorInput) obj;
    if (!areEqual(name, other.name)) {
      return false;
    }
    return areEqual(fileUri, other.fileUri);
  }

  @Override public int hashCode() {
    final int prime = HASH_CODE_PRIME;
    int result = 1;
    result = prime * result + hashCodeOf(name);
    result = prime * result + hashCodeOf(fileUri);
    return result;
  }
}

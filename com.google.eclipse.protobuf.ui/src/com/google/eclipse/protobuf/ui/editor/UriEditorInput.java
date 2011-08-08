/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

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
  public Object getAdapter(Class adapter) {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  public boolean exists() {
    return false;
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  public String getName() {
    return name;
  }

  public IPersistableElement getPersistable() {
    return null;
  }

  public String getToolTipText() {
    return name;
  }

  public URI getFileUri() {
    return fileUri;
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((fileUri == null) ? 0 : fileUri.hashCode());
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    UriEditorInput other = (UriEditorInput) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (fileUri == null) {
      if (other.fileUri != null) return false;
    } else if (!fileUri.equals(other.fileUri)) return false;
    return true;
  }
}

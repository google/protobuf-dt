/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.google.eclipse.protobuf.junit.stubs;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MarkerStub implements IMarker {

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    return null;
  }

  /** {@inheritDoc} */
  public void delete() throws CoreException {}

  /** {@inheritDoc} */
  public boolean exists() {
    return false;
  }

  /** {@inheritDoc} */
  public Object getAttribute(String attributeName) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public int getAttribute(String attributeName, int defaultValue) {
    return 0;
  }

  /** {@inheritDoc} */
  public String getAttribute(String attributeName, String defaultValue) {
    return null;
  }

  /** {@inheritDoc} */
  public boolean getAttribute(String attributeName, boolean defaultValue) {
    return false;
  }

  /** {@inheritDoc} */
  public Map<String, Object> getAttributes() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public Object[] getAttributes(String[] attributeNames) throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public long getCreationTime() throws CoreException {
    return 0;
  }

  /** {@inheritDoc} */
  public long getId() {
    return 0;
  }

  /** {@inheritDoc} */
  public IResource getResource() {
    return null;
  }

  /** {@inheritDoc} */
  public String getType() throws CoreException {
    return null;
  }

  /** {@inheritDoc} */
  public boolean isSubtypeOf(String superType) throws CoreException {
    return false;
  }

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, int value) throws CoreException {}

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, Object value) throws CoreException {}

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, boolean value) throws CoreException {}

  /** {@inheritDoc} */
  public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {}

  /** {@inheritDoc} */
  public void setAttributes(Map<String, ? extends Object> attributes) throws CoreException {}
}

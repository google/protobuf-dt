/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs;

import static java.util.Collections.unmodifiableMap;

import java.util.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MarkerStub implements IMarker {

  private final Map<String, Object> attributes = new HashMap<String, Object>();
  private final String type;
  private final long creationTime;

  public MarkerStub(String type) {
    this.type = type;
    creationTime = System.currentTimeMillis();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void delete() throws CoreException {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public boolean exists() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public Object getAttribute(String attributeName) throws CoreException {
    return attributes.get(attributeName);
  }

  /** {@inheritDoc} */
  public int getAttribute(String attributeName, int defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Integer) return (Integer) attribute;
    return defaultValue;
  }

  /** {@inheritDoc} */
  public String getAttribute(String attributeName, String defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof String) return (String) attribute;
    return defaultValue;
  }

  /** {@inheritDoc} */
  public boolean getAttribute(String attributeName, boolean defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Boolean) return (Boolean) attribute;
    return defaultValue;
  }

  /** {@inheritDoc} */
  public Map<String, Object> getAttributes() throws CoreException {
    return unmodifiableMap(attributes);
  }

  /** {@inheritDoc} */
  public Object[] getAttributes(String[] attributeNames) throws CoreException {
    List<Object> values = new ArrayList<Object>();
    for (String name : attributeNames) values.add(attributes.get(name));
    return values.toArray();
  }

  /** {@inheritDoc} */
  public long getCreationTime() throws CoreException {
    return creationTime;
  }

  /** {@inheritDoc} */
  public long getId() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public IResource getResource() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public String getType() throws CoreException {
    return type;
  }

  /** {@inheritDoc} */
  public boolean isSubtypeOf(String superType) throws CoreException {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, int value) throws CoreException {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, Object value) throws CoreException {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  public void setAttribute(String attributeName, boolean value) throws CoreException {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void setAttributes(Map<String, ? extends Object> attributes) throws CoreException {
    this.attributes.putAll(attributes);
  }
}

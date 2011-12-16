/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs.resources;

import static com.google.eclipse.protobuf.util.Objects.*;
import static java.util.Collections.unmodifiableMap;

import java.util.*;

import org.eclipse.core.resources.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MarkerStub implements IMarker {

  private final Map<String, Object> attributes = new HashMap<String, Object>();
  private final String type;
  private final long creationTime;

  public static MarkerStub error(String type, String description, int lineNumber) {
    MarkerStub marker = new MarkerStub(type);
    marker.setAttribute(SEVERITY, SEVERITY_ERROR);
    marker.setAttribute(MESSAGE, description);
    marker.setAttribute(LINE_NUMBER, lineNumber);
    return marker;
  }

  public MarkerStub(String type) {
    this.type = type;
    creationTime = System.currentTimeMillis();
  }

  /** {@inheritDoc} */
  @Override @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void delete() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean exists() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Object getAttribute(String attributeName) {
    return attributes.get(attributeName);
  }

  /** {@inheritDoc} */
  @Override public int getAttribute(String attributeName, int defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Integer) {
      return (Integer) attribute;
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override public String getAttribute(String attributeName, String defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof String) {
      return (String) attribute;
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override public boolean getAttribute(String attributeName, boolean defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Boolean) {
      return (Boolean) attribute;
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override public Map<String, Object> getAttributes() {
    return unmodifiableMap(attributes);
  }

  /** {@inheritDoc} */
  @Override public Object[] getAttributes(String[] attributeNames) {
    List<Object> values = new ArrayList<Object>();
    for (String name : attributeNames) {
      values.add(attributes.get(name));
    }
    return values.toArray();
  }

  /** {@inheritDoc} */
  @Override public long getCreationTime() {
    return creationTime;
  }

  /** {@inheritDoc} */
  @Override public long getId() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public IResource getResource() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getType() {
    return type;
  }

  /** {@inheritDoc} */
  @Override public boolean isSubtypeOf(String superType) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setAttribute(String attributeName, int value) {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  @Override public void setAttribute(String attributeName, Object value) {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  @Override public void setAttribute(String attributeName, boolean value) {
    attributes.put(attributeName, value);
  }

  /** {@inheritDoc} */
  @Override public void setAttributes(String[] attributeNames, Object[] values) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setAttributes(Map<String, ? extends Object> attributes) {
    this.attributes.putAll(attributes);
  }

  public int severity() {
    return getAttribute(SEVERITY, -1);
  }

  public String message() {
    return (String) getAttribute(MESSAGE);
  }

  public int lineNumber() {
    return getAttribute(LINE_NUMBER, -1);
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MarkerStub other = (MarkerStub) obj;
    if (!areEqual(attributes, other.attributes)) {
      return false;
    }
    return areEqual(type, other.type);
  }

  @Override public int hashCode() {
    final int prime = HASH_CODE_PRIME;
    int result = 1;
    result = prime * result + hashCodeOf(attributes);
    result = prime * result + hashCodeOf(type);
    return result;
  }

  @Override public String toString() {
    return "MarkerStub [attributes=" + attributes + "]";
  }
}

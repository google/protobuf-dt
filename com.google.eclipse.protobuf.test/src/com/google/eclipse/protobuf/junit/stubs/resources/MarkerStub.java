/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs.resources;

import static java.util.Collections.unmodifiableMap;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import com.google.common.base.Objects;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MarkerStub implements IMarker {
  private final Map<String, Object> attributes = newHashMap();
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

  @Override @SuppressWarnings("rawtypes") public Object getAdapter(Class adapter) {
    throw new UnsupportedOperationException();
  }

  @Override public void delete() {
    throw new UnsupportedOperationException();
  }

  @Override public boolean exists() {
    throw new UnsupportedOperationException();
  }

  @Override public Object getAttribute(String attributeName) {
    return attributes.get(attributeName);
  }

  @Override public int getAttribute(String attributeName, int defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Integer) {
      return (Integer) attribute;
    }
    return defaultValue;
  }

  @Override public String getAttribute(String attributeName, String defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof String) {
      return (String) attribute;
    }
    return defaultValue;
  }

  @Override public boolean getAttribute(String attributeName, boolean defaultValue) {
    Object attribute = attributes.get(attributeName);
    if (attribute instanceof Boolean) {
      return (Boolean) attribute;
    }
    return defaultValue;
  }

  @Override public Map<String, Object> getAttributes() {
    return unmodifiableMap(attributes);
  }

  @Override public Object[] getAttributes(String[] attributeNames) {
    List<Object> values = newArrayList();
    for (String name : attributeNames) {
      values.add(attributes.get(name));
    }
    return values.toArray();
  }

  @Override public long getCreationTime() {
    return creationTime;
  }

  @Override public long getId() {
    throw new UnsupportedOperationException();
  }

  @Override public IResource getResource() {
    throw new UnsupportedOperationException();
  }

  @Override public String getType() {
    return type;
  }

  @Override public boolean isSubtypeOf(String superType) {
    throw new UnsupportedOperationException();
  }

  @Override public void setAttribute(String attributeName, int value) {
    attributes.put(attributeName, value);
  }

  @Override public void setAttribute(String attributeName, Object value) {
    attributes.put(attributeName, value);
  }

  @Override public void setAttribute(String attributeName, boolean value) {
    attributes.put(attributeName, value);
  }

  @Override public void setAttributes(String[] attributeNames, Object[] values) {
    throw new UnsupportedOperationException();
  }

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
    if (!equal(attributes, other.attributes)) {
      return false;
    }
    return equal(type, other.type);
  }

  @Override public int hashCode() {
    return Objects.hashCode(attributes, type);
  }

  @Override public String toString() {
    return "MarkerStub [attributes=" + attributes + "]";
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs.resources;

import static org.eclipse.emf.common.util.URI.createURI;

import java.io.*;
import java.util.Map;

import org.eclipse.emf.common.notify.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceStub implements Resource {
  private URI uri;

  public ResourceStub() {}

  public ResourceStub(String uri) {
    setURI(createURI(uri));
  }

  /** {@inheritDoc} */
  @Override public EList<Adapter> eAdapters() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean eDeliver() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void eSetDeliver(boolean deliver) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void eNotify(Notification notification) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public ResourceSet getResourceSet() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public URI getURI() {
    return uri;
  }

  /** {@inheritDoc} */
  @Override public void setURI(URI uri) {
    this.uri = uri;
  }

  /** {@inheritDoc} */
  @Override public long getTimeStamp() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setTimeStamp(long timeStamp) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EList<EObject> getContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public TreeIterator<EObject> getAllContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public String getURIFragment(EObject eObject) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EObject getEObject(String uriFragment) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void save(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void load(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void save(OutputStream outputStream, Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void load(InputStream inputStream, Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isTrackingModification() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setTrackingModification(boolean isTrackingModification) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isModified() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void setModified(boolean isModified) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean isLoaded() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void unload() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void delete(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EList<Diagnostic> getErrors() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EList<Diagnostic> getWarnings() {
    throw new UnsupportedOperationException();
  }

}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs;

import static org.eclipse.emf.common.util.URI.createURI;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

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
  public EList<Adapter> eAdapters() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public boolean eDeliver() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void eSetDeliver(boolean deliver) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void eNotify(Notification notification) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public ResourceSet getResourceSet() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public URI getURI() {
    return uri;
  }

  /** {@inheritDoc} */
  public void setURI(URI uri) {
    this.uri = uri;
  }

  /** {@inheritDoc} */
  public long getTimeStamp() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void setTimeStamp(long timeStamp) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public EList<EObject> getContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public TreeIterator<EObject> getAllContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public String getURIFragment(EObject eObject) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public EObject getEObject(String uriFragment) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void save(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void load(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void save(OutputStream outputStream, Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void load(InputStream inputStream, Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public boolean isTrackingModification() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void setTrackingModification(boolean isTrackingModification) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public boolean isModified() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void setModified(boolean isModified) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public boolean isLoaded() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void unload() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public void delete(Map<?, ?> options) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public EList<Diagnostic> getErrors() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  public EList<Diagnostic> getWarnings() {
    throw new UnsupportedOperationException();
  }

}

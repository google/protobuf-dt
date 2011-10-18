/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.stubs.protobuf;

import org.eclipse.emf.common.notify.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.protobuf.Package;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PackageStub implements Package {

  private String name;

  public PackageStub() {}

  public PackageStub(String name) {
    this.name = name;
  }

  /** {@inheritDoc} */
  @Override public EClass eClass() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Resource eResource() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EObject eContainer() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EStructuralFeature eContainingFeature() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EReference eContainmentFeature() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EList<EObject> eContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public TreeIterator<EObject> eAllContents() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean eIsProxy() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public EList<EObject> eCrossReferences() {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Object eGet(EStructuralFeature feature) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Object eGet(EStructuralFeature feature, boolean resolve) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void eSet(EStructuralFeature feature, Object newValue) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public boolean eIsSet(EStructuralFeature feature) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public void eUnset(EStructuralFeature feature) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override public Object eInvoke(EOperation operation, EList<?> arguments) {
    throw new UnsupportedOperationException();
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
  @Override public String getName() {
    return name;
  }

  /** {@inheritDoc} */
  @Override public void setName(String value) {
    name = value;
  }

}

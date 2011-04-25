/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.stubs;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.impl.EnumImpl;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EnumStub extends EnumImpl implements EContainerStub {

  private EList<EObject> contents = new BasicEList<EObject>();

  public EnumStub(String name) {
    this.name = name;
  }

  public void add(EObjectStub...children) {
    for (EObjectStub child : children) {
      contents.add(child);
      child.setContainer(this);
    }
  }
  
  @Override public EList<EObject> eContents() {
    return contents;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.model.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import com.google.eclipse.protobuf.protobuf.Package;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class PackageFinder {

  public static Package findPackage(Name name, Root root) {
    for (Package aPackage : getAllContentsOfType(root.value, Package.class))
      if (name.value.equals(aPackage.getName())) return aPackage;
    return null;
  }

  private PackageFinder() {}
}

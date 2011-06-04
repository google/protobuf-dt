/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.xtext.naming.*;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class PackageResolver {

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  boolean areRelated(Package p1, Package p2) {
    if (p1 == null || p2 == null) return false;
    QualifiedName name1 = nameOf(p1);
    QualifiedName name2 = nameOf(p2);
    if (name1 == null || name2 == null) return false;
    if (name1.equals(name2)) return true;
    return false;
  }

  private QualifiedName nameOf(Package p) {
    String name = p.getName();
    if (isEmpty(name)) return null;
    return converter.toQualifiedName(name);
  }
}

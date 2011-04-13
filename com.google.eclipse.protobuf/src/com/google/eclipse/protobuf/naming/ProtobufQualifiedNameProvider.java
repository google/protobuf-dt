/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.EObjectFinder;
import com.google.inject.Inject;

/**
 * Provides fully-qualified names for protobuf elements.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQualifiedNameProvider extends DefaultDeclarativeQualifiedNameProvider {

  @Inject private EObjectFinder finder;

  /** {@inheritDoc} */
  @Override public QualifiedName getFullyQualifiedName(EObject obj) {
    QualifiedName fqn = super.getFullyQualifiedName(obj);
    if (fqn == null || obj instanceof Package) return fqn;
    Package p = finder.findPackage(obj);
    if (p == null) return fqn;
    List<String> segments = new ArrayList<String>(fqn.getSegments());
    String packageName = p.getName();
    if (packageName != null && !segments.isEmpty() && !packageName.equals(segments.get(0))) 
      segments.add(0, packageName);
    return QualifiedName.create(segments.toArray(new String[segments.size()]));
  }
}

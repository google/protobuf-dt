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
    QualifiedName qualifiedName = super.getFullyQualifiedName(obj);
    if (qualifiedName == null || obj instanceof Package) return qualifiedName;
    Package p = finder.findPackage(obj);
    if (p == null) return qualifiedName;
    List<String> newQualifiedNameSegments = new ArrayList<String>();
    List<String> qualifiedNameSegments = qualifiedName.getSegments();
    String packageName = p.getName();
    if (packageName != null) {
      String[] packageNameSegments = packageName.split("\\.");
      if (!qualifiedNameContainsPackageName(qualifiedNameSegments, packageNameSegments)) {
        // add package to the new FQN
        for (String packageSegment : packageNameSegments) newQualifiedNameSegments.add(packageSegment);
      }
    }
    newQualifiedNameSegments.addAll(qualifiedNameSegments);
    return QualifiedName.create(newQualifiedNameSegments.toArray(new String[newQualifiedNameSegments.size()]));
  }

  private boolean qualifiedNameContainsPackageName(List<String> qualifiedNameSegments, String[] packageNameSegments) {
    int fqnLength = qualifiedNameSegments.size();
    int packageSegmentCount = packageNameSegments.length;
    if (fqnLength <= packageSegmentCount) return false;
    for (int i = 0; i < fqnLength; i++) {
      if (i == packageSegmentCount) break;
      if (!qualifiedNameSegments.get(i).equals(packageNameSegments[i])) return false;
    }
    return true;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptySet;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.model.util.QualifiedNames;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PackageIntersectionDescriptions {
  @Inject private Packages packages;
  @Inject private QualifiedNames qualifiedNames;
  @Inject private IQualifiedNameProvider nameProvider;

  // See issue 161
  Collection<IEObjectDescription> intersection(Package fromImporter, Package fromImported, EObject e) {
    if (fromImporter == null || fromImported == null) {
      return emptySet();
    }
    return intersection2(segmentNames(fromImporter), segmentNames(fromImported), e);
  }

  private List<String> segmentNames(Package aPackage) {
    return packages.segmentsOf(aPackage);
  }

  private Collection<IEObjectDescription> intersection2(List<String> packageInImporter, List<String> packageInImported,
      EObject e) {
    int n1Count = packageInImporter.size();
    int n2Count = packageInImported.size();
    int start = -1;
    for (int i = 0; (i < n1Count && i < n2Count); i++) {
      if (!packageInImporter.get(i).equals(packageInImported.get(i))) {
        start = i;
        break;
      }
    }
    if (start == 0) {
      return emptySet(); // no intersection found.
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(e);
    List<String> segments = newArrayList(qualifiedName.getSegments());
    for (int i = 0; i < start; i++) {
      segments.remove(0);
      QualifiedName newQualifiedName = qualifiedNames.createQualifiedName(segments);
      descriptions.add(create(newQualifiedName, e));
    }
    return descriptions;
  }
}

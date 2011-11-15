/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.*;
import static org.eclipse.xtext.resource.EObjectDescription.create;
import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;

import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.protobuf.Package;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PackageIntersectionDescriptions {

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  private final Function<EObject, String> resolver = newResolver(String.class, "name");

  Collection<IEObjectDescription> intersection(Package fromImporter, Package fromImported, EObject e) {
    if (fromImporter == null || fromImported == null) return emptySet();
    return intersection(segmentNames(fromImporter), segmentNames(fromImported), e);
  }
  
  private List<String> segmentNames(Package aPackage) {
    return converter.toQualifiedName(aPackage.getName()).getSegments();
  }
  
  private Collection<IEObjectDescription> intersection(List<String> packageInImporter, List<String> packageInImported,
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
    if (start == 0) return emptySet(); // no intersection found.
    List<String> intersection = new ArrayList<String>();
    intersection.add(resolver.apply(e));
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (int i = n2Count - 1; i >= 0; i--) {
      if (i >= start) {
        intersection.add(0, packageInImported.get(i));
        continue;
      }
      if (i == start - 1) {
        descriptions.add(create(fqn(intersection), e));
      }
      intersection.add(0, packageInImported.get(i));
      descriptions.add(create(fqn(intersection), e));
    }
    return descriptions;
  }
  
  private QualifiedName fqn(List<String> segments) {
    return QualifiedName.create(segments.toArray(new String[segments.size()]));
  }
}

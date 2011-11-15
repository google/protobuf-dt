/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.resource.EObjectDescription.create;
import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class IntersectedPackageDescriptions {

  private final Function<EObject, String> resolver = newResolver(String.class, "name");

  IEObjectDescription description(EObject e, List<String> packageFqn) {
    if (packageFqn.isEmpty()) return null;
    String name = resolver.apply(e);
    QualifiedName fqn = createFqn(name, packageFqn);
    return create(fqn, e);
  }
  
  @VisibleForTesting 
  QualifiedName createFqn(String name, List<String> packageFqn) {
    List<String> nameSegments = new ArrayList<String>(packageFqn);
    nameSegments.add(name);
    return QualifiedName.create(nameSegments.toArray(new String[nameSegments.size()]));
  }
}

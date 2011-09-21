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

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNameDescriptions {

  @Inject private IQualifiedNameProvider nameProvider;
  @Inject private QualifiedNames qualifiedNames;

  Collection<IEObjectDescription> qualifiedNames(EObject obj) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    QualifiedName fqn = nameProvider.getFullyQualifiedName(obj);
    descriptions.add(create(fqn, obj));
    descriptions.add(create(qualifiedNames.addLeadingDot(fqn), obj));
    return descriptions;
  }
}

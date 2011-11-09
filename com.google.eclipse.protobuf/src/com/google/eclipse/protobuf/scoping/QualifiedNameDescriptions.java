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

import com.google.eclipse.protobuf.naming.Naming.NameTarget;
import com.google.eclipse.protobuf.naming.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNameDescriptions {

  @Inject private ProtobufQualifiedNameProvider nameProvider;
  @Inject private QualifiedNames qualifiedNames;

  Collection<IEObjectDescription> qualifiedNames(EObject e, NameTarget target) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    QualifiedName fqn = nameProvider.getFullyQualifiedName(e, target);
    descriptions.add(create(fqn, e));
    descriptions.add(create(qualifiedNames.addLeadingDot(fqn), e));
    return descriptions;
  }
}

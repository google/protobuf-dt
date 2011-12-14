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

import com.google.eclipse.protobuf.model.util.QualifiedNames;
import com.google.eclipse.protobuf.naming.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNameDescriptions {

  @Inject private IProtobufQualifiedNameProvider nameProvider;
  @Inject private QualifiedNames qualifiedNames;

  Collection<IEObjectDescription> qualifiedNames(EObject e) {
    QualifiedName fqn = nameProvider.getFullyQualifiedName(e);
    return allQualifiedNames(e, fqn);
  }

  Collection<IEObjectDescription> qualifiedNamesForOption(EObject e) {
    QualifiedName fqn = nameProvider.getFullyQualifiedNameForOption(e);
    return allQualifiedNames(e, fqn);
  }

  private Collection<IEObjectDescription> allQualifiedNames(EObject e, QualifiedName fqn) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    descriptions.add(create(fqn, e));
    descriptions.add(create(qualifiedNames.addLeadingDot(fqn), e));
    return descriptions;
  }
}

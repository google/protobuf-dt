/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptyList;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Literal;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class LiteralDescriptions {
  Collection<IEObjectDescription> literalsOf(Enum anEnum) {
    if (anEnum == null) {
      return emptyList();
    }
    List<IEObjectDescription> descriptions = newArrayList();
    for (Literal literal : getAllContentsOfType(anEnum, Literal.class)) {
      String name = literal.getName();
      descriptions.add(create(name, literal));
    }
    return descriptions;
  }
}

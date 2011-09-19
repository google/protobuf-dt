/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class LiteralDescriptions {

  Collection<IEObjectDescription> literalsOf(Enum anEnum) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : getAllContentsOfType(anEnum, Literal.class))
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }
}

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

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.EObjectFinder;
import com.google.inject.Inject;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private EObjectFinder finder;
  @Inject private Globals globals;

  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject container = literalRef.eContainer();
    if (container instanceof Property) {
      Enum enumType = finder.enumTypeOfProperty((Property) container);
      if (enumType != null) return scopeForLiteralsIn(enumType);
    }
    if (container instanceof Option && globals.isOptimizeForOption((Option) container)) {
      Enum optimizedMode = globals.optimizedMode();
      return scopeForLiteralsIn(optimizedMode);
    }
    return null;
  }

  private static IScope scopeForLiteralsIn(Enum enumType) {
    List<IEObjectDescription> descriptions = descriptionsFrom(enumType);
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }

  private static List<IEObjectDescription> descriptionsFrom(Enum enumType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : enumType.getLiterals())
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }
}

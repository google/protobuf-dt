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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ContainAllLiteralsInEnum extends BaseMatcher<IEObjectDescriptions> {

  private final Enum anEnum;

  static ContainAllLiteralsInEnum containAllLiteralsIn(Enum anEnum) {
    return new ContainAllLiteralsInEnum(anEnum);
  }
  
  private ContainAllLiteralsInEnum(Enum anEnum) {
    this.anEnum = anEnum;
  }
  
  public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    List<Literal> literals = allLiterals();
    if (descriptions.size() != literals.size()) return false;
    for (Literal literal : literals) {
      String name = literal.getName();
      EObject described = descriptions.objectDescribedAs(name);
      if (described != literal) return false;
    }
    return true;
  }

  public void describeTo(Description description) {
    List<String> names = new ArrayList<String>();
    for (Literal literal : allLiterals()) {
      names.add(literal.getName());
    }
    description.appendValue(names);
  }

  private List<Literal> allLiterals() {
    return getAllContentsOfType(anEnum, Literal.class);
  }
}

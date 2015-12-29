/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.junit.IEObjectDescriptions;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Literal;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllLiteralsInEnum extends TypeSafeMatcher<IEObjectDescriptions> {
  private final Enum anEnum;

  public static ContainAllLiteralsInEnum containAllLiteralsIn(Enum anEnum) {
    return new ContainAllLiteralsInEnum(anEnum);
  }

  private ContainAllLiteralsInEnum(Enum anEnum) {
    super(IEObjectDescriptions.class);
    this.anEnum = anEnum;
  }

  @Override public boolean matchesSafely(IEObjectDescriptions item) {
    List<Literal> literals = allLiterals();
    if (item.size() != literals.size()) {
      return false;
    }
    for (Literal literal : literals) {
      String name = literal.getName();
      EObject described = item.objectDescribedAs(name);
      if (described != literal) {
        return false;
      }
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    Collection<String> names = transform(allLiterals(), new Function<Literal, String>() {
      @Override public String apply(Literal input) {
        return input.getName();
      }
    });
    description.appendValue(names);
  }

  private List<Literal> allLiterals() {
    return getAllContentsOfType(anEnum, Literal.class);
  }
}

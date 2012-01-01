/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.*;

import org.hamcrest.*;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EnumHasLiterals extends BaseMatcher<Enum> {
  private final String[] literalNames;

  public static EnumHasLiterals hasLiterals(String... literalNames) {
    return new EnumHasLiterals(literalNames);
  }

  private EnumHasLiterals(String... literalNames) {
    this.literalNames = literalNames;
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof Enum)) {
      return false;
    }
    Enum anEnum = (Enum) arg;
    List<String> actualNames = newArrayList(literalNames(anEnum));
    for (String name : literalNames) {
      actualNames.remove(name);
    }
    return actualNames.isEmpty();
  }

  private Collection<String> literalNames(Enum anEnum) {
    List<Literal> allLiterals = getAllContentsOfType(anEnum, Literal.class);
    return transform(allLiterals, new Function<Literal, String>() {
      @Override public String apply(Literal input) {
        return input.getName();
      }
    });
  }

  @Override public void describeTo(Description description) {
    description.appendValue(Arrays.toString(literalNames));
  }
}

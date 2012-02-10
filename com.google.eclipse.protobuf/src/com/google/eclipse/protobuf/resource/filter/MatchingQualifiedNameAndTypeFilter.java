/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource.filter;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.base.Predicate;

/**
 * Indicates whether the qualified name and {@code EClass} of a <code>{@link IEObjectDescription}</code> match the given
 * pattern and type, respectively.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MatchingQualifiedNameAndTypeFilter implements Predicate<IEObjectDescription> {
  private final Pattern pattern;
  private final EClass type;

  /**
   * Creates a new <code>{@link MatchingQualifiedNameAndTypeFilter}</code>.
   * @param pattern the pattern that qualified names should match.
   * @param type the type of model object to match.
   * @return the created filter.
   */
  public static MatchingQualifiedNameAndTypeFilter matchingQualifiedNameAndType(Pattern pattern, EClass type) {
    return new MatchingQualifiedNameAndTypeFilter(pattern, type);
  }

  private MatchingQualifiedNameAndTypeFilter(Pattern pattern, EClass type) {
    this.pattern = pattern;
    this.type = type;
  }

  @Override public boolean apply(IEObjectDescription input) {
    if (!type.equals(input.getEClass())) {
      return false;
    }
    QualifiedName qualifiedName = input.getQualifiedName();
    return pattern.matcher(qualifiedName.toString()).matches();
  }
}

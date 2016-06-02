/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * Utility methods related to <code>{@link Package}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Packages {
  @Inject private IQualifiedNameConverter qualifiedNameConverter;

  /**
   * Indicates whether the given packages are "related." "Related" means that the names of the packages are equal or one
   * package is a sub-package of the other one.
   * @param p1 package to verify against {@code p2}.
   * @param p2 package to verify against {@code p1}.
   * @return {@code true} if the given packages are related; {@code false} otherwise.
   */
  public boolean areRelated(Package p1, Package p2) {
    if (p1 == null || p2 == null) {
      return false;
    }
    QualifiedName name1 = nameOf(p1);
    QualifiedName name2 = nameOf(p2);
    if (name1 == null || name2 == null) {
      return false;
    }
    if (name1.equals(name2)) {
      return true;
    }
    return (isSubPackage(name1, name2));
  }

  private boolean isSubPackage(QualifiedName name1, QualifiedName name2) {
    List<String> segments = name2.getSegments();
    int segment2Count = segments.size();
    int counter = 0;
    for (String segment1 : name1.getSegments()) {
      if (!segment1.equals(segments.get(counter++))) {
        return false;
      }
      if (counter == segment2Count) {
        break;
      }
    }
    return true;
  }

  /**
   * Adds the segments in the name of the given package to the given qualified name, creating a new qualified name per
   * segment.
   * <p>
   * For example, given the following proto element:
   *
   * <pre>
   * package test.alternative.names;
   *
   * message Person {
   *   optional string name = 1;
   *
   *   enum PhoneType {
   *     HOME = 0;
   *     WORK = 1;
   *   }
   * }
   * </pre>
   *
   * This method will create the following qualified names for {@code PhoneType}:
   * <ul>
   * <li>{@code PhoneType}</li>
   * <li>{@code Person.PhoneType}</li>
   * <li>{@code names.Person.PhoneType}</li>
   * <li>{@code test.names.Person.PhoneType}</li>
   * </ul>
   * </p>
   * @param p the given package.
   * @param name the base name.
   * @return a collection containing the created qualified names, or an empty list if the name of the given package
   * contains zero or one segments.
   */
  public Collection<QualifiedName> addPackageNameSegments(Package p, QualifiedName name) {
    QualifiedName current = name;
    List<String> segments = segmentsOf(p);
    int segmentCount = segments.size();
    if (segmentCount <= 1) {
      return emptyList();
    }
    List<QualifiedName> allNames = newArrayList();
    for (int i = segmentCount - 1; i > 0; i--) {
      current = QualifiedName.create(segments.get(i)).append(current);
      allNames.add(current);
    }
    return unmodifiableList(allNames);
  }

  /**
   * Returns the segments in the name of the given package.
   * @param p the given package.
   * @return the segments in the name of the given package.
   */
  public List<String> segmentsOf(Package p) {
    QualifiedName name = (p == null) ? null : nameOf(p);
    if (name == null) {
      return emptyList();
    }
    return name.getSegments();
  }

  private QualifiedName nameOf(Package p) {
    String name = p.getName();
    return (isEmpty(name)) ? null : qualifiedNameConverter.toQualifiedName(name);
  }
}

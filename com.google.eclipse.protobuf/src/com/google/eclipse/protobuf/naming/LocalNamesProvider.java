/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.*;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.inject.Inject;

/**
 * Provides alternative qualified names for protobuf elements.
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
 * The default qualified name for {@code PhoneType} is {@code alternative.names.Person.PhoneType}. The problem is that
 * protoc also recognizes the following as qualified names:
 * <ul>
 * <li>{@code PhoneType}</li>
 * <li>{@code Person.PhoneType}</li>
 * <li>{@code names.Person.PhoneType}</li>
 * <li>{@code test.names.Person.PhoneType}</li>
 * </ul>
 * </p>
 * <p>
 * This class provides the non-default qualified names recognized by protoc.
 * </p>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LocalNamesProvider {
  @Inject private ModelObjects modelObjects;
  @Inject private NameResolver nameResolver;
  @Inject private IQualifiedNameConverter qualifiedNameConverter;
  @Inject private Packages packages;

  public List<QualifiedName> localNames(EObject e, NamingStrategy namingStrategy) {
    List<QualifiedName> allNames = newArrayList();
    EObject current = e;
    String name = namingStrategy.nameOf(e);
    if (isEmpty(name)) {
      return emptyList();
    }
    QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(name);
    allNames.add(qualifiedName);
    while (current.eContainer() != null) {
      current = current.eContainer();
      String containerName = nameResolver.nameOf(current);
      if (isEmpty(containerName)) {
        continue;
      }
      qualifiedName = qualifiedNameConverter.toQualifiedName(containerName).append(qualifiedName);
      allNames.add(qualifiedName);
    }
    allNames.addAll(packages.addPackageNameSegments(modelObjects.packageOf(e), qualifiedName));
    return unmodifiableList(allNames);
  }
}

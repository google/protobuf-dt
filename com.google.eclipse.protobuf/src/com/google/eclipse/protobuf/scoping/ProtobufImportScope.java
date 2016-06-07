/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.resource.impl.AliasedEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportScope;

import com.google.common.collect.Lists;

public class ProtobufImportScope extends ImportScope {
  private final EClass type;

  public ProtobufImportScope(
      List<ImportNormalizer> namespaceResolvers,
      IScope parent,
      ISelectable importFrom,
      EClass type,
      boolean ignoreCase) {
    super(namespaceResolvers, parent, importFrom, type, ignoreCase);
    this.type = type;
  }

  @Override
  protected Iterable<IEObjectDescription> getAliasedElements(
      Iterable<IEObjectDescription> candidates) {
    ArrayList<IEObjectDescription> descriptions =
        Lists.newArrayList(super.getAliasedElements(candidates));
    for (IEObjectDescription imported : candidates) {
      descriptions.add(new AliasedEObjectDescription(addLeadingDot(imported.getName()), imported));
    }
    return descriptions;
  }

  @Override
  protected Iterable<IEObjectDescription> getLocalElementsByName(QualifiedName name) {
    List<IEObjectDescription> result =
        (List<IEObjectDescription>) super.getLocalElementsByName(name);
    QualifiedName resolvedQualifiedName = null;
    final QualifiedName resolvedName = name.skipFirst(1);
    ISelectable importFrom = getImportFrom();
    if (resolvedName != null) {
      Iterable<IEObjectDescription> resolvedElements =
          importFrom.getExportedObjects(type, resolvedName, isIgnoreCase());
      for (IEObjectDescription resolvedElement : resolvedElements) {
        if (resolvedQualifiedName == null) resolvedQualifiedName = resolvedName;
        else if (!resolvedQualifiedName.equals(resolvedName)) {
          if (result.get(0).getEObjectOrProxy() != resolvedElement.getEObjectOrProxy()) {
            return emptyList();
          }
        }
        QualifiedName alias = addLeadingDot(resolvedElement.getName());
        final AliasedEObjectDescription aliasedEObjectDescription =
            new AliasedEObjectDescription(alias, resolvedElement);
        result.add(aliasedEObjectDescription);
      }
    }
    return result;
  }

  private QualifiedName addLeadingDot(QualifiedName qualifiedName) {
    return QualifiedName.create("").append(qualifiedName);
  }
}

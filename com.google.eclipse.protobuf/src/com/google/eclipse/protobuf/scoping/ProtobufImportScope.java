/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.model.util.QualifiedNames.removeLeadingDot;

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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * {@link ImportScope} that allows additional ImportNormalizers to be added after initialization.
 *
 * @author (atrookey@google.com) Alexander Rookey
 */
public class ProtobufImportScope extends ImportScope {
  private final EClass type;
  private List<ImportNormalizer> normalizers;

  public ProtobufImportScope(
      List<ImportNormalizer> namespaceResolvers,
      IScope parent,
      ISelectable importFrom,
      EClass type,
      boolean ignoreCase) {
    super(namespaceResolvers, parent, importFrom, type, ignoreCase);
    this.type = type;
    this.normalizers = removeDuplicates(namespaceResolvers);
  }

  @Override
  protected Iterable<IEObjectDescription> getAliasedElements(
      Iterable<IEObjectDescription> candidates) {
    Multimap<QualifiedName, IEObjectDescription> keyToDescription = LinkedHashMultimap.create();
    Multimap<QualifiedName, ImportNormalizer> keyToNormalizer = HashMultimap.create();

    for (IEObjectDescription imported : candidates) {
      QualifiedName fullyQualifiedName = imported.getName();
      for (ImportNormalizer normalizer : normalizers) {
        QualifiedName alias = normalizer.deresolve(fullyQualifiedName);
        if (alias != null) {
          QualifiedName key = alias;
          if (isIgnoreCase()) {
            key = key.toLowerCase();
          }
          keyToDescription.put(key, new AliasedEObjectDescription(alias, imported));
          keyToNormalizer.put(key, normalizer);
        }
      }
    }
    for (QualifiedName name : keyToNormalizer.keySet()) {
      if (keyToNormalizer.get(name).size() > 1) keyToDescription.removeAll(name);
    }
    return keyToDescription.values();
  }

  // TODO (atrookey) Refactor this method for clarity
  @Override
  protected Iterable<IEObjectDescription> getLocalElementsByName(QualifiedName name) {
    List<IEObjectDescription> result = new ArrayList<>();
    QualifiedName resolvedQualifiedName = null;
    ISelectable importFrom = getImportFrom();
    for (ImportNormalizer normalizer : normalizers) {
      final QualifiedName resolvedName = normalizer.resolve(name);
      if (resolvedName != null) {
        Iterable<IEObjectDescription> resolvedElements =
            importFrom.getExportedObjects(type, resolvedName, isIgnoreCase());
        for (IEObjectDescription resolvedElement : resolvedElements) {
          if (resolvedQualifiedName == null) {
            resolvedQualifiedName = resolvedName;
          } else if (!resolvedQualifiedName.equals(resolvedName)) {
            if (result.get(0).getEObjectOrProxy() != resolvedElement.getEObjectOrProxy()) {
              continue;
            }
          }
          QualifiedName alias = normalizer.deresolve(resolvedElement.getName());
          if (alias == null)
            throw new IllegalStateException(
                "Couldn't deresolve " + resolvedElement.getName() + " with import " + normalizer);
          final AliasedEObjectDescription aliasedEObjectDescription =
              new AliasedEObjectDescription(alias, resolvedElement);
          result.add(aliasedEObjectDescription);
        }
      }
    }
    return result;
  }

  /** Before getting element, check for and remove leading dot. */
  @Override
  public IEObjectDescription getSingleElement(QualifiedName name) {
    return super.getSingleElement(removeLeadingDot(name));
  }

  public void addNormalizer(ImportNormalizer normalizer) {
    normalizers.add(normalizer);
  }
}

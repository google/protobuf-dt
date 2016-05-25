/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.impl.ImportNormalizer;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.util.Strings;
import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameConverter;
import com.google.inject.Inject;

public class ProtobufImportedNamespaceAwareLocalScopeProvider
    extends ImportedNamespaceAwareLocalScopeProvider {
  @Inject private ProtobufQualifiedNameConverter qualifiedNameConverter;

  /**
   * Creates a new {@link ImportNormalizer} for the given namespace.
   *
   * @param namespace the namespace.
   * @param ignoreCase <code>true</code> if the resolver should be case insensitive.
   * @return a new {@link ImportNormalizer} or {@code null} if the namespace cannot be
   *     converted to a valid qualified name.
   */
  @Override
  protected ImportNormalizer createImportedNamespaceResolver(String namespace,
      boolean ignoreCase) {
    if (Strings.isEmpty(namespace)) {
      return null;
    }
    QualifiedName importedNamespace = qualifiedNameConverter.toQualifiedName(namespace);
    if (importedNamespace == null || importedNamespace.isEmpty()) {
      return null;
    }
    return doCreateImportNormalizer(importedNamespace, true, ignoreCase);
  }
}

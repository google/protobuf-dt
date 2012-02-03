/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.fqn;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * Provides all the possible qualified names of the protocol buffer element used to generate a C++ element.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class QualifiedNameProvider {
  private final Map<Class<?>, QualifiedNameProviderStrategy<?>> strategies = newHashMap();

  public QualifiedNameProvider() {
    add(new ClassTypeQualifiedNameProviderStrategy());
    add(new EnumQualifiedNameProviderStrategy());
  }

  private void add(QualifiedNameProviderStrategy<?> strategy) {
    strategies.put(strategy.supportedBindingType(), strategy);
  }

  /**
   * Returns a lazy-loaded <code>{@link Iterable}</code> containing all the possible qualified names of the protocol
   * buffer element used to generate a C++ element.
   * @param binding specifies the semantics of the name of the generated C++ element.
   * @return a lazy-loaded {@code Iterable} containing all the possible qualified names, or {@code null} if qualified
   * names cannot be obtained from the given {@code IBinding}.
   */
  public Iterable<QualifiedName> qualifiedNamesFrom(IBinding binding) {
    QualifiedNameProviderStrategy<? extends IBinding> strategy = strategies.get(binding.getClass());
    return (strategy != null) ? strategy.qualifiedNamesFrom(binding) : null;
  }
}

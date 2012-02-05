/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IBinding;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class CppToProtobufMapper {
  private final Map<Class<? extends IBinding>, IBindingMappingStrategy<?>> strategies = newHashMap();

  public CppToProtobufMapper() {
    register(new ClassMappingStrategy());
    register(new EnumMappingStrategy());
  }

  private void register(IBindingMappingStrategy<?> strategy) {
    strategies.put(strategy.typeOfSupportedBinding(), strategy);
  }

  /**
   * Creates a <code>{@link CppToProtobufMapping}</code> from the C++ element whose name is described by the given
   * {@code IBinding}.
   * @param binding describes the name of a C++ element.
   * @return a {@code ProtobufElementLookupInfo}, or {@code null} if the given binding does not correspond to a C++
   * element that can be traced back to a protocol buffer element.
   */
  public CppToProtobufMapping createMappingFrom(IBinding binding) {
    IBindingMappingStrategy<?> strategy = strategies.get(binding.getClass());
    return (strategy != null) ? strategy.createMappingFrom(binding) : null;
  }
}

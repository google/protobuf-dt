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
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton public class CppToProtobufMapper {
  private final Map<Class<? extends IBinding>, IBindingMappingStrategy<?>> strategies = newHashMap();

  @Inject public CppToProtobufMapper(ClassMappingStrategy s1, EnumMappingStrategy s2, MethodMappingStrategy s3,
      TypeDefMappingStrategy s4) {
    register(s1);
    register(s2);
    register(s3);
    register(s4);
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
    IBinding bindingToUse = binding;
    if (binding instanceof ProblemBinding) {
      ProblemBinding problemBinding = (ProblemBinding) binding;
      IBinding[] candidates = problemBinding.getCandidateBindings();
      if (candidates != null && candidates.length == 1) {
        bindingToUse = candidates[0];
      }
    }
    IBindingMappingStrategy<?> strategy = strategies.get(bindingToUse.getClass());
    if (strategy != null) {
      return strategy.createMappingFrom(bindingToUse);
    }
    return null;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class MethodMappingStrategy implements IBindingMappingStrategy<CPPMethod> {
  @Inject private IBindings bindings;

  @Override public CppToProtobufMapping createMappingFrom(IBinding binding) {
    CPPMethod method = typeOfSupportedBinding().cast(binding);
    ICPPFunctionType type = method.getType();
    if (!type.isConst()) {
      return null;
    }
    IType[] types = type.getParameterTypes();
    if (types != null && types.length > 0) {
      return null;
    }
    return new CppToProtobufMapping(bindings.qualifiedNameOf(method), MESSAGE_FIELD);
  }

  @Override public Class<CPPMethod> typeOfSupportedBinding() {
    return CPPMethod.class;
  }
}

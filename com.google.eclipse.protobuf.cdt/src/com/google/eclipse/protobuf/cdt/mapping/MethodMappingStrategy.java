/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class MethodMappingStrategy implements IBindingMappingStrategy<CPPMethod> {

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
    QualifiedName qualifiedName = QualifiedName.create(method.getQualifiedName());
    return new CppToProtobufMapping(qualifiedName, MessageField.class);
  }

  @Override public Class<CPPMethod> typeOfSupportedBinding() {
    return CPPMethod.class;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class ClassMappingStrategy implements IBindingMappingStrategy<CPPClassType> {
  @Inject private IBindings bindings;

  @Override public CppToProtobufMapping createMappingFrom(IBinding binding) {
    CPPClassType classType = typeOfSupportedBinding().cast(binding);
    if (bindings.isMessage(classType)) {
      return new CppToProtobufMapping(bindings.qualifiedNameOf(classType), MESSAGE);
    }
    return null;
  }

  @Override public Class<CPPClassType> typeOfSupportedBinding() {
    return CPPClassType.class;
  }
}

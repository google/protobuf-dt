/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.ENUM;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumeration;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class EnumMappingStrategy implements IBindingMappingStrategy<CPPEnumeration> {

  @Override public CppToProtobufMapping createMappingFrom(IBinding binding) {
    CPPEnumeration enumeration = typeOfSupportedBinding().cast(binding);
    return new CppToProtobufMapping(enumeration.getQualifiedName(), ENUM);
  }

  @Override public Class<CPPEnumeration> typeOfSupportedBinding() {
    return CPPEnumeration.class;
  }
}

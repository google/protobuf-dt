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
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class TypeDefMappingStrategy implements IBindingMappingStrategy<CPPTypedef> {
  @Inject private IBindings bindings;

  @Override public CppToProtobufMapping createMappingFrom(IBinding binding) {
    CPPTypedef typeDef = typeOfSupportedBinding().cast(binding);
    IBinding owner = binding.getOwner();
    if (!bindings.isMessage(owner)) {
      return null;
    }
    String typeName = typeNameOf(typeDef);
    String typeNameSuffix = owner.getName() + "_" + typeDef.getName();
    if (typeName == null || !typeName.endsWith(typeNameSuffix)) {
      return null;
    }
    return new CppToProtobufMapping(bindings.qualifiedNameOf(typeDef), MESSAGE);
  }

  private String typeNameOf(CPPTypedef typeDef) {
    IType type = typeDef.getType();
    if (type instanceof CPPClassType) {
      return ((CPPClassType) type).getName();
    }
    return null;
  }

  @Override public Class<CPPTypedef> typeOfSupportedBinding() {
    return CPPTypedef.class;
  }
}

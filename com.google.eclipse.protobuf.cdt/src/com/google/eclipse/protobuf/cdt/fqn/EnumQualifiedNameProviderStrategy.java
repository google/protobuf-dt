/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.fqn;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumeration;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class EnumQualifiedNameProviderStrategy implements QualifiedNameProviderStrategy<CPPEnumeration> {
  @Override public Iterable<QualifiedName> qualifiedNamesFrom(IBinding binding) {
    CPPEnumeration enumeration = supportedBindingType().cast(binding);
    String[] segments = enumeration.getQualifiedName();
    return new QualifiedNameSource(segments);
  }

  @Override public Class<CPPEnumeration> supportedBindingType() {
    return CPPEnumeration.class;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.fqn;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class ClassTypeQualifiedNameProviderStrategy implements QualifiedNameProviderStrategy<CPPClassType> {
  @Override public Iterable<QualifiedName> qualifiedNamesFrom(IBinding binding) {
    CPPClassType classType = supportedBindingType().cast(binding);
    if (isMessage(classType)) {
      String[] segments = classType.getQualifiedName();
      return new QualifiedNameSource(segments);
    }
    return null;
  }

  private boolean isMessage(CPPClassType classType) {
    ICPPBase[] bases = classType.getBases();
    if (bases.length != 1) {
      return false;
    }
    IName name = bases[0].getBaseClassSpecifierName();
    if (!(name instanceof CPPASTQualifiedName)) {
      return false;
    }
    CPPASTQualifiedName qualifiedName = (CPPASTQualifiedName) name;
    if (!qualifiedName.isFullyQualified()) {
      return false;
    }
    String qualifiedNameAsText = qualifiedName.toString();
    return "::google::protobuf::Message".equals(qualifiedNameAsText);
  }

  @Override public Class<CPPClassType> supportedBindingType() {
    return CPPClassType.class;
  }
}

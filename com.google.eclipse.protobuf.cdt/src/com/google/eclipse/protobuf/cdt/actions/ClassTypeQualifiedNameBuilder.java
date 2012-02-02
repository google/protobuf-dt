/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static java.util.Collections.*;

import java.util.Collection;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class ClassTypeQualifiedNameBuilder {
  public Collection<QualifiedName> createQualifiedNamesFrom(CPPClassType classType) {
    if (isMessage(classType)) {
      String[] segments = classType.getQualifiedName();
      return singletonList(QualifiedName.create(segments));
    }
    return emptyList();
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
    String rawSignature = qualifiedName.getRawSignature();
    return "::google::protobuf::Message".equals(rawSignature);
  }
}

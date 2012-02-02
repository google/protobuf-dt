/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static java.util.Collections.emptyList;

import java.util.List;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class ClassTypeQualifiedNameBuilder {
  @Inject QualifiedNameFactory qualifiedNameFactory;

  public List<QualifiedName> createQualifiedNamesFrom(CPPClassType classType) {
    if (isMessage(classType)) {
      String[] segments = classType.getQualifiedName();
      return qualifiedNameFactory.createQualifiedNamesForComplexType(segments);
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
    String qualifiedNameAsText = qualifiedName.toString();
    return "::google::protobuf::Message".equals(qualifiedNameAsText);
  }
}

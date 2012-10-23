/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static java.util.Collections.unmodifiableList;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;

import com.google.common.collect.ImmutableList;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class IBindings {
  private static final ImmutableList<String> MESSAGE_SUPER_TYPES = of("::google::protobuf::Message",
      "google::protobuf::MessageLite");

  boolean isMessage(IBinding binding) {
    if (!(binding instanceof CPPClassType)) {
      return false;
    }
    CPPClassType classType = (CPPClassType) binding;
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
    return MESSAGE_SUPER_TYPES.contains(qualifiedNameAsText);
  }

  List<String> qualifiedNameOf(IBinding binding) {
    List<String> segments = newArrayList();
    for (IBinding owner = binding.getOwner(); owner != null; owner = owner.getOwner()) {
      if (owner instanceof ICPPEnumeration && !((ICPPEnumeration) owner).isScoped()) {
        continue;
      }
      String ownerName = owner.getName();
      if (ownerName == null) {
        break;
      }
      if (owner instanceof ICPPFunction || owner instanceof ICPPNamespace) {
        continue;
      }
      segments.add(0, ownerName);
    }
    segments.add(binding.getName());
    return unmodifiableList(segments);
  }
}

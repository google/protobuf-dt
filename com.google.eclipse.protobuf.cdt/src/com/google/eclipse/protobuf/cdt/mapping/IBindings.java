/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Collections.unmodifiableList;

import com.google.common.collect.ImmutableList;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IBindings {
  public boolean isMessage(IBinding binding) {
    if (!(binding instanceof CPPClassType)) {
      return false;
    }
    CPPClassType classType = (CPPClassType) binding;
    ICPPBase[] bases = classType.getBases();
    if (bases.length != 1) {
      return false;
    }
    ICPPBinding baseClass = (ICPPBinding) bases[0].getBaseClass();
    String name = baseClass.getName();
    if (!name.equals("Message") && !name.equals("MessageLite")) {
      return false;
    }
    IBinding owner = baseClass.getOwner();
    if (!(owner instanceof ICPPNamespace)) {
      return false;
    }
    if (!owner.getName().equals("protobuf")) {
      return false;
    }
    owner = owner.getOwner();
    if (!(owner instanceof ICPPNamespace)) {
      return false;
    }
    if (!owner.getName().equals("google")) {
      return false;
    }
    return true;
  }

  public List<String> qualifiedNameOf(IBinding binding) {
    List<String> segments = new ArrayList<>();
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

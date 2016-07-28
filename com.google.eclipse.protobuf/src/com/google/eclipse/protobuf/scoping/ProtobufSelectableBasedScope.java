/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.model.util.QualifiedNames.removeLeadingDot;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.ISelectable;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SelectableBasedScope;

import com.google.common.base.Predicate;

/**
 * {@link SelectableBasedScope} that handles qualified names with a leading dot.
 *
 * @author (atrookey@google.com) Alexander Rookey
 */
public class ProtobufSelectableBasedScope extends SelectableBasedScope {
  public static IScope createScope(
      IScope outer, ISelectable selectable, EClass type, boolean ignoreCase) {
    return createScope(outer, selectable, null, type, ignoreCase);
  }

  public static IScope createScope(
      IScope outer,
      ISelectable selectable,
      Predicate<IEObjectDescription> filter,
      EClass type,
      boolean ignoreCase) {
    if (selectable == null || selectable.isEmpty()) {
      return outer;
    }
    return new ProtobufSelectableBasedScope(outer, selectable, filter, type, ignoreCase);
  }

  protected ProtobufSelectableBasedScope(
      IScope outer,
      ISelectable selectable,
      Predicate<IEObjectDescription> filter,
      EClass type,
      boolean ignoreCase) {
    super(outer, selectable, filter, type, ignoreCase);
  }

  /** Before getting element, removes leading dot. */
  @Override
  public IEObjectDescription getSingleElement(QualifiedName name) {
    return super.getSingleElement(removeLeadingDot(name));
  }
}

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
import org.eclipse.xtext.naming.QualifiedName;

/**
 * Provides all the possible qualified names of the protocol buffer element used to generate a C++ element described
 * by a specific type of <code>{@link IBinding}</code>.
 * @param <T> the type of {@code IBinding} this strategy supports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
interface QualifiedNameProviderStrategy<T extends IBinding> {
  Iterable<QualifiedName> qualifiedNamesFrom(IBinding binding);

  Class<T> supportedBindingType();
}

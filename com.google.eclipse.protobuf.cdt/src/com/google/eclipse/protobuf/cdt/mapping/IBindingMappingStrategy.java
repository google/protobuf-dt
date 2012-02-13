/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import org.eclipse.cdt.core.dom.ast.IBinding;

/**
 * Derives a <code>{@link CppToProtobufMapping}</code> from the C++ element whose name is described by the given
 * {@code IBinding}.
 * @param <T> the type of {@code IBinding} supported.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
interface IBindingMappingStrategy<T extends IBinding> {
  CppToProtobufMapping createMappingFrom(IBinding binding);

  Class<T> typeOfSupportedBinding();
}

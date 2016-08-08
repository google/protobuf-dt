/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.xtext.generator.scoping.AbstractScopingFragment;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScopeProvider;

public class ProtobufImportNamespacesScopingFragment extends AbstractScopingFragment {
  @Override
  protected Class<? extends IGlobalScopeProvider> getGlobalScopeProvider() {
    return ProtobufImportUriGlobalScopeProvider.class;
  }

  @Override
  protected Class<? extends IScopeProvider> getLocalScopeProvider() {
    return ProtobufImportedNamespaceAwareLocalScopeProvider.class;
  }
}

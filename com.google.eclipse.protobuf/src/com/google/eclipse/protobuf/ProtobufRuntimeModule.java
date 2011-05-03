/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameProvider;
import com.google.eclipse.protobuf.scoping.SimpleImportUriResolver;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class ProtobufRuntimeModule extends com.google.eclipse.protobuf.AbstractProtobufRuntimeModule {

  /** {@inheritDoc} */
  @Override public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
    return ProtobufQualifiedNameProvider.class;
  }

  public void configureImportUriResolver(Binder binder) {
    binder.bind(ImportUriResolver.class).to(SimpleImportUriResolver.class);
  }
}

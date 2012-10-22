/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import com.google.eclipse.protobuf.conversion.ProtobufTerminalConverters;
import com.google.eclipse.protobuf.linking.ProtobufResource;
import com.google.eclipse.protobuf.naming.*;
import com.google.eclipse.protobuf.resource.*;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.validation.*;
import com.google.inject.Binder;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.eclipse.xtext.validation.IResourceValidator;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class ProtobufRuntimeModule extends AbstractProtobufRuntimeModule {
  public Class<? extends IGlobalServiceProvider> bindIGlobalServiceProvider() {
    return GlobalResourceServiceProvider.class;
  }

  public Class<? extends ImportUriResolver> bindImportUriResolver() {
    return ProtobufImportUriResolver.class;
  }

  public Class<? extends IQualifiedNameConverter> bindIQualifiedNameConverter() {
    return ProtobufQualifiedNameConverter.class;
  }

  @Override public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
    return ProtobufQualifiedNameProvider.class;
  }

  public Class<? extends IResourceValidator> bindIResourceValidator() {
    return ProtobufResourceValidator.class;
  }

  public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
    return ProtobufSyntaxErrorMessageProvider.class;
  }

  @Override public Class<? extends IValueConverterService> bindIValueConverterService() {
    return ProtobufTerminalConverters.class;
  }

  @Override public Class<? extends XtextResource> bindXtextResource() {
    return ProtobufResource.class;
  }

  @Override public Class<? extends XtextResourceSet> bindXtextResourceSet() {
    return FastXtextResourceSet.class;
  }

  public void configureExtensionRegistry(Binder binder) {
    binder.bind(IExtensionRegistry.class).toProvider(ExtensionRegistryProvider.class);
  }
}

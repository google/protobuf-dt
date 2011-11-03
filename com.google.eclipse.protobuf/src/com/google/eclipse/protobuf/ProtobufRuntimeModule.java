/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.IGlobalServiceProvider;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.conversion.ProtobufTerminalConverters;
import com.google.eclipse.protobuf.naming.ProtobufQualifiedNameProvider;
import com.google.eclipse.protobuf.parser.Proto2OnlyParser;
import com.google.eclipse.protobuf.resource.ResourceServiceProvider;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.validation.*;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class ProtobufRuntimeModule extends com.google.eclipse.protobuf.AbstractProtobufRuntimeModule {

  public Class<? extends IGlobalServiceProvider> bindIGlobalServiceProvider() {
    return ResourceServiceProvider.class;
  }
  
  public Class<? extends ImportUriResolver> bindImportUriResolver() {
    return ProtobufImportUriResolver.class;
  }
  
  @Override public Class<? extends org.eclipse.xtext.parser.IParser> bindIParser() {
    return Proto2OnlyParser.class;
  }

  @Override public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
    return ProtobufQualifiedNameProvider.class;
  }

  public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
    return ProtobufSyntaxErrorMessageProvider.class;
  }
  
  @Override public Class<? extends IValueConverterService> bindIValueConverterService() {
    return ProtobufTerminalConverters.class;
  }
  
  public void configureExtensionRegistry(Binder binder) {
    binder.bind(IExtensionRegistry.class).toProvider(ExtensionRegistryProvider.class);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.eclipse.protobuf.junit.core.GeneratedProtoFiles.protoFile;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.*;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.inject.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IntegrationTestSetup extends ProtobufStandaloneSetup {

  IntegrationTestSetup() {}
  
  @Override
  public Injector createInjector() {
    return Guice.createInjector(new Module());
  }
  
  private static class Module extends ProtobufRuntimeModule {
    @SuppressWarnings("unused")
    public Class<? extends IFileUriResolver> bindFileUriResolver() {
      return FileUriResolver.class;
    }
  }
  
  private static class FileUriResolver implements IFileUriResolver {
    @Override public String resolveUri(String importUri, Resource declaringResource) {
      URI uri = URI.createURI(importUri);
      if (!isEmpty(uri.scheme())) return importUri; // already resolved.
      File file = protoFile(importUri);
      if (!file.exists()) throw new IllegalArgumentException("File: " + importUri + " does not exist.");
      return file.toURI().toString();
    }
  }
}

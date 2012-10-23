/*
* Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.junit.core.GeneratedProtoFiles.protoFile;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;

/**
 * Guice module for unit testing.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IntegrationTestModule extends AbstractTestModule {
  public static IntegrationTestModule integrationTestModule() {
    return new IntegrationTestModule();
  }

  private IntegrationTestModule( ) {}

  @Override protected void configure() {
    binder().bind(IFileUriResolver.class).to(FileUriResolver.class);
    mockAndBind(EReference.class);
  }

  private static class FileUriResolver implements IFileUriResolver {
    @Override public void resolveAndUpdateUri(Import anImport) {
      String importUri = anImport.getImportURI();
      URI uri = URI.createURI(importUri);
      if (!isEmpty(uri.scheme()))
      {
        return; // already resolved.
      }
      File file = protoFile(importUri);
      if (!file.exists()) {
        return; // file does not exist.
      }
      String resolvedUri = file.toURI().toString();
      anImport.setImportURI(resolvedUri);
    }
  }
}

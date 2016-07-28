/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.eclipse.protobuf.junit.core.GeneratedProtoFiles.protoFile;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IImportResolver;
import com.google.eclipse.protobuf.scoping.IUriResolver;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.mockito.Mockito;

import java.io.File;

/**
 * Guice module for unit testing.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IntegrationTestModule extends AbstractTestModule {
  public static IntegrationTestModule integrationTestModule() {
    return new IntegrationTestModule();
  }

  private IntegrationTestModule() {}

  @Override
  protected void configure() {
    binder().bind(IImportResolver.class).to(ImportResolver.class);
    mockAndBind(EReference.class);
    IPreferenceStoreAccess mockStoreAccess = Mockito.mock(IPreferenceStoreAccess.class);
    IUriResolver mockUriResolver = Mockito.mock(IUriResolver.class);
    binder().bind(IPreferenceStoreAccess.class).toInstance(mockStoreAccess);
    // TODO (atrookey) Get rid of the excessive mocking.
    binder().bind(IUriResolver.class).toInstance(mockUriResolver);
    IPreferenceStore mockPreferenceStore = Mockito.mock(IPreferenceStore.class);
    Mockito.when(mockStoreAccess.getWritablePreferenceStore(Mockito.anyObject()))
        .thenReturn(mockPreferenceStore);
    Mockito.when(mockPreferenceStore.getString(PreferenceNames.DESCRIPTOR_PROTO_PATH))
        .thenReturn(PreferenceNames.DEFAULT_DESCRIPTOR_PATH);
    Mockito.when(
            mockUriResolver.resolveUri(
                Mockito.eq(PreferenceNames.DEFAULT_DESCRIPTOR_PATH),
                Mockito.anyObject(),
                Mockito.anyObject()))
        .thenReturn("platform:/plugin/com.google.eclipse.protobuf/descriptor.proto");
  }

  private static class ImportResolver implements IImportResolver {
    @Inject private Imports imports;

    @Override
    public String resolve(Import anImport) {
      String importUri = imports.getPath(anImport);
      URI uri = URI.createURI(importUri);
      if (!isEmpty(uri.scheme())) {
        return importUri; // already resolved.
      }
      File file = protoFile(importUri);
      if (!file.exists()) {
        return null; // file does not exist.
      }
      return file.toURI().toString();
    }

    @Override
    public void invalidateCacheFor(Import anImport) {}
  }
}

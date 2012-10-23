/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt;

import static com.google.eclipse.protobuf.cdt.ProtobufObjectsProvider.getfromProtobufPlugin;

import org.eclipse.xtext.service.AbstractGenericModule;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.resource.IndexLookup;
import com.google.eclipse.protobuf.util.StringLists;
import com.google.inject.Binder;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufCdtModule extends AbstractGenericModule {
  public void configureModelObjectDefinitionNavigator(Binder binder) {
    bindToTypeInProtobufPlugin(IndexLookup.class, binder);
    bindToTypeInProtobufPlugin(IPreferenceStoreAccess.class, binder);
    bindToTypeInProtobufPlugin(IResourceSetProvider.class, binder);
    bindToTypeInProtobufPlugin(IURIEditorOpener.class, binder);
    bindToTypeInProtobufPlugin(ModelObjects.class, binder);
    bindToTypeInProtobufPlugin(Packages.class, binder);
    bindToTypeInProtobufPlugin(Resources.class, binder);
    bindToTypeInProtobufPlugin(StringLists.class, binder);
  }

  private <T> void bindToTypeInProtobufPlugin(Class<T> type, Binder binder) {
    binder.bind(type).toProvider(getfromProtobufPlugin(type));
  }
}

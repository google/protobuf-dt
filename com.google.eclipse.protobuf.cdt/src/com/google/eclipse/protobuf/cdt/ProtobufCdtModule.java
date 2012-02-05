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

import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.resource.*;
import com.google.inject.Binder;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufCdtModule extends AbstractGenericModule {
  public void configureModelObjectDefinitionNavigator(Binder binder) {
    bindToProtobufPluginObject(IndexLookup.class, binder);
    bindToProtobufPluginObject(IPreferenceStoreAccess.class, binder);
    bindToProtobufPluginObject(IQualifiedNameConverter.class, binder);
    bindToProtobufPluginObject(IURIEditorOpener.class, binder);
    bindToProtobufPluginObject(ModelObjects.class, binder);
    bindToProtobufPluginObject(ResourceDescriptions.class, binder);
    bindToProtobufPluginObject(Resources.class, binder);
  }

  private <T> void bindToProtobufPluginObject(Class<T> type, Binder binder) {
    binder.bind(type).toProvider(getfromProtobufPlugin(type));
  }
}

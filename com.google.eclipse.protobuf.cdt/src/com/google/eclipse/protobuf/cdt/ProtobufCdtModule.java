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
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator;
import com.google.inject.Binder;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufCdtModule extends AbstractGenericModule {
  public void configureModelObjectDefinitionNavigator(Binder binder) {
    bindToProtobufPluginObject(IPreferenceStoreAccess.class, binder);
    bindToProtobufPluginObject(ModelObjectDefinitionNavigator.class, binder);
  }

  private <T> void bindToProtobufPluginObject(Class<T> type, Binder binder) {
    binder.bind(type).toProvider(getfromProtobufPlugin(type));
  }
}

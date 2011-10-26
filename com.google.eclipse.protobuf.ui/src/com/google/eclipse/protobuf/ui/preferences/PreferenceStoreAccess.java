/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.preferences.*;

import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.inject.*;
import com.google.inject.name.Named;

/**
 * Workaround for bug in Xtext where a project's preference store is never used.
 * 
 * TODO verify if this bug is fixed in Xtext 2.0.1.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class PreferenceStoreAccess implements IPreferenceStoreAccess {

  private boolean initialized = false;

  @Inject private IPreferenceStoreInitializer.CompositeImpl initializer;

  private String qualifier;

  @Override public IPreferenceStore getContextPreferenceStore(Object context) {
    lazyInitialize();
    return new ChainedPreferenceStore(new IPreferenceStore[] { getWritablePreferenceStore(context),
        ProtobufActivator.getInstance().getPreferenceStore(), EditorsUI.getPreferenceStore()});
  }

  @Override public IPreferenceStore getPreferenceStore() {
    lazyInitialize();
    return new ChainedPreferenceStore(new IPreferenceStore[] { getWritablePreferenceStore(),
        ProtobufActivator.getInstance().getPreferenceStore(), EditorsUI.getPreferenceStore()});
  }

  protected String getQualifier() {
    return qualifier;
  }

  @Override public IPreferenceStore getWritablePreferenceStore() {
    lazyInitialize();
    ScopedPreferenceStore result = new ScopedPreferenceStore(InstanceScope.INSTANCE, getQualifier());
    result.setSearchContexts(new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE });
    return result;
  }

  @Override public IPreferenceStore getWritablePreferenceStore(Object context) {
    lazyInitialize();
    Object finalContext = context;
    if (finalContext instanceof IFileEditorInput) {
      finalContext = ((IFileEditorInput) context).getFile().getProject();
    }
    if (finalContext instanceof IProject) {
      ProjectScope projectScope = new ProjectScope((IProject) finalContext);
      ScopedPreferenceStore result = new ScopedPreferenceStore(projectScope, getQualifier());
      result.setSearchContexts(new IScopeContext[] { projectScope, InstanceScope.INSTANCE, ConfigurationScope.INSTANCE });
      return result;
    }
    return getWritablePreferenceStore();
  }

  protected void lazyInitialize() {
    if (!initialized) {
      initialized = true;
      initializer.initialize(this);
    }
  }

  @Inject public void setLanguageNameAsQualifier(@Named(Constants.LANGUAGE_NAME) String languageName) {
    this.qualifier = languageName;
  }
}

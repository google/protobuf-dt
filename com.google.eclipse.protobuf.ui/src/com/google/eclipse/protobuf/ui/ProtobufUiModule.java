/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui;

import static com.google.inject.name.Names.named;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

import com.google.eclipse.protobuf.ui.builder.AutoAddNatureEditorCallback;
import com.google.eclipse.protobuf.ui.outline.LinkWithEditor;
import com.google.eclipse.protobuf.ui.outline.ProtobufOutlinePage;
import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferencesInitializer;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferencesInitializer;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used within the IDE.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufUiModule extends AbstractProtobufUiModule {

  public ProtobufUiModule(AbstractUIPlugin plugin) {
    super(plugin);
  }

  @Override public Class<? extends IContentOutlinePage> bindIContentOutlinePage() {
    return ProtobufOutlinePage.class;
  }

  @Override public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
    return AutoAddNatureEditorCallback.class;
  }

  /** {@inheritDoc} */
  @Override public void configureToggleLinkWithEditorOutlineContribution(Binder binder) {
    binder.bind(IOutlineContribution.class)
          .annotatedWith(IOutlineContribution.LinkWithEditor.class)
          .to(LinkWithEditor.class);
  }

  public void configureCompilerPreferencesInitializer(Binder binder) {
    configurePreferenceInitializer(binder, "compilerPreferences", CompilerPreferencesInitializer.class);
  }

  public void configurePathsPreferencesInitializer(Binder binder) {
    configurePreferenceInitializer(binder, "pathsPreferences", PathsPreferencesInitializer.class);
  }
  
  private void configurePreferenceInitializer(Binder binder, String name,
      Class<? extends IPreferenceStoreInitializer> initializerType) {
    binder.bind(IPreferenceStoreInitializer.class).annotatedWith(named(name)).to(initializerType);
  }
}

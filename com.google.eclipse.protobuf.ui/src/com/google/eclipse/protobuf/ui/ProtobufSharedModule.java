/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui;

import com.google.eclipse.protobuf.ui.builder.ProtobufBuilderState;
import com.google.inject.*;
import com.google.inject.name.Names;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.eclipse.xtext.builder.clustering.ClusteringBuilderState;
import org.eclipse.xtext.builder.impl.*;
import org.eclipse.xtext.builder.resourceloader.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.notification.*;
import org.eclipse.xtext.ui.resource.*;
import org.eclipse.xtext.ui.shared.JdtHelper;
import org.eclipse.xtext.ui.shared.internal.SharedModule;
import org.eclipse.xtext.ui.util.IJdtHelper;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufSharedModule extends SharedModule {

  @Override protected void configure() {
    bind(IBuilderState.class).to(ProtobufBuilderState.class).in(Scopes.SINGLETON);
    bind(IResourceDescriptions.class).to(DirtyStateAwareResourceDescriptions.class).in(Scopes.SINGLETON);
    bind(IResourceServiceProvider.Registry.class).toInstance(IResourceServiceProvider.Registry.INSTANCE);
    bind(IResourceSetProvider.class).to(SimpleResourceSetProvider.class);
    bind(IExtensionRegistry.class).toInstance(Platform.getExtensionRegistry());
    bind(IResourceChangeListener.class).annotatedWith(Names.named(ProjectOpenedOrClosedListener.class.getName()))
        .to(ProjectOpenedOrClosedListener.class);

    bind(IExternalContentSupport.IExternalContentProvider.class).to(IDirtyStateManager.class).in(Scopes.SINGLETON);
    bind(IDirtyStateManager.class).to(DirtyStateManager.class).in(Scopes.SINGLETON);
    bind(IStateChangeEventBroker.class).to(StateChangeEventBroker.class).in(Scopes.SINGLETON);

    bind(IncrementalProjectBuilder.class).to(XtextBuilder.class);
    bind(IStorage2UriMapper.class).to(Storage2UriMapperImpl.class).in(Scopes.SINGLETON);

    bind(IWorkbench.class).toProvider(new Provider<IWorkbench>() {
      @Override public IWorkbench get() {
        if (PlatformUI.isWorkbenchRunning()) {
          return PlatformUI.getWorkbench();
        }
        return null;
      }
    });

    bind(IWorkspace.class).toProvider(new Provider<IWorkspace>() {
      @Override public IWorkspace get() {
        return ResourcesPlugin.getWorkspace();
      }
    });

    bind(IJdtHelper.class).to(JdtHelper.class).asEagerSingleton();

    boolean parallel = false;
    if (parallel) {
      bind(IResourceLoader.class).toProvider(ResourceLoaderProviders.getParallelLoader());

      bind(IResourceLoader.class).annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_GLOBAL_INDEX))
          .toProvider(ResourceLoaderProviders.getParallelLoader());

      bind(IResourceLoader.class).annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_CROSS_LINKING))
          .toProvider(ResourceLoaderProviders.getParallelLoader());
    } else {
      bind(IResourceLoader.class).toProvider(ResourceLoaderProviders.getSerialLoader());

      bind(IResourceLoader.class).annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_GLOBAL_INDEX))
          .toProvider(ResourceLoaderProviders.getSerialLoader());

      bind(IResourceLoader.class).annotatedWith(Names.named(ClusteringBuilderState.RESOURCELOADER_CROSS_LINKING))
          .toProvider(ResourceLoaderProviders.getSerialLoader());
    }

  }
}

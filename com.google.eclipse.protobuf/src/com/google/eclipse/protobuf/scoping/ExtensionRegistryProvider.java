/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Provider of instances of <code>{@link IExtensionRegistry}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ExtensionRegistryProvider implements Provider<IExtensionRegistry> {
  @Override public IExtensionRegistry get() {
    return Platform.getExtensionRegistry();
  }
}

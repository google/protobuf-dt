/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.nature.resourceloader;

import org.eclipse.xtext.builder.resourceloader.*;
import org.eclipse.xtext.builder.resourceloader.ResourceLoaderProviders.AbstractResourceLoaderProvider;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufResourceLoaderProvider extends AbstractResourceLoaderProvider {
  @Override public IResourceLoader get() {
    return new ProtobufSerialResourceLoader(getResourceSetProvider(), getResourceSorter());
  }
}

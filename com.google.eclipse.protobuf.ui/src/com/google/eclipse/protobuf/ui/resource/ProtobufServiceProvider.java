/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.impl.DefaultResourceServiceProvider;

import com.google.eclipse.protobuf.resource.IResourceVerifier;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufServiceProvider extends DefaultResourceServiceProvider {
  @Inject private IResourceVerifier resourceVerifier;

  @Override public boolean canHandle(URI uri) {
    if (!super.canHandle(uri)) {
      return false;
    }
    return !resourceVerifier.shouldIgnore(uri);
  }
}

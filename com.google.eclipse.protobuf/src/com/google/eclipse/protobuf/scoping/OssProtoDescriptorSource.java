/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.inject.Singleton;

import org.eclipse.emf.common.util.URI;

import java.io.*;
import java.net.URL;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class OssProtoDescriptorSource implements IProtoDescriptorSource {

  private static final String DESCRIPTOR_URL = "platform:/plugin/com.google.eclipse.protobuf/descriptor.proto";
  
  private static final URI DESCRIPTOR_URI = URI.createURI(DESCRIPTOR_URL);

  public URI uri() {
    return DESCRIPTOR_URI;
  }

  public InputStream contents() throws IOException {
    URL url = new URL(DESCRIPTOR_URL);
    return url.openConnection().getInputStream();
  }

}

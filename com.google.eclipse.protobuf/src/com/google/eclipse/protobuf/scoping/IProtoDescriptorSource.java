/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.inject.ImplementedBy;

import org.eclipse.emf.common.util.URI;

import java.io.*;

/**
 * Provides the contents of <code>{@link ProtoDescriptor}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(OssProtoDescriptorSource.class)
public interface IProtoDescriptorSource {

  /**
   * Returns the URI of the resource containing the descriptor.
   * @return the URI of the resource containing the descriptor.
   */
  URI uri();
  
  /**
   * Returns the contents of the descriptor.
   * @return the contents of the descriptor.
   * @throws IOException if something goes wrong.
   */
  InputStream contents() throws IOException;
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.*;

import org.eclipse.xtext.parser.IParser;

/**
 * Provider of a singleton instance of <code>{@link ProtoDescriptor}</code>.
 *
 * @author Alex Ruiz
 */
@Singleton
public class ProtoDescriptorProvider implements Provider<IProtoDescriptor> {

  @Inject private IParser parser;
  @Inject private ModelNodes nodes;

  private IProtoDescriptor descriptor;

  public synchronized IProtoDescriptor get() {
    if (descriptor == null) descriptor = new ProtoDescriptor(parser, nodes);
    return descriptor;
  }
}

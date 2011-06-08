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
public class ProtoDescriptorProvider implements Provider<ProtoDescriptor> {

  @Inject private IParser parser;
  @Inject private IProtoDescriptorSource source;
  @Inject private ModelNodes nodes;

  private ProtoDescriptor descriptor;

  public synchronized ProtoDescriptor get() {
    if (descriptor == null) descriptor = new ProtoDescriptor(parser, source, nodes);
    return descriptor;
  }
}

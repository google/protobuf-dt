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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.*;
import org.eclipse.xtext.parser.IParser;

/**
 * Provider of a singleton instance of <code>{@link ProtoDescriptor}</code>.
 *
 * @author Alex Ruiz
 */
@Singleton
public class ProtoDescriptorProvider implements Provider<ProtoDescriptor> {

  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.descriptorSource";

  private static Logger logger = Logger.getLogger(ProtoDescriptorProvider.class);
  
  @Inject private IParser parser;
  @Inject private IProtoDescriptorSource source;
  @Inject private ModelNodes nodes;
  @Inject private IExtensionRegistry registry;

  private ProtoDescriptor descriptor;

  public synchronized ProtoDescriptor get() {
    IProtoDescriptorSource actualSource = sourceFromPlugin();
    if (actualSource == null) actualSource = source;
    if (descriptor == null) descriptor = new ProtoDescriptor(parser, actualSource, nodes);
    return descriptor;
  }
  
  private IProtoDescriptorSource sourceFromPlugin() {
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    try {
      for (IConfigurationElement e : config) {
        Object extension = e.createExecutableExtension("class");
        if (extension instanceof IProtoDescriptorSource) return (IProtoDescriptorSource) extension;
      }
    } catch (CoreException t) {
      logger.fatal("Unable to instantiate extension elements", t);
    }
    return null;
  }
}

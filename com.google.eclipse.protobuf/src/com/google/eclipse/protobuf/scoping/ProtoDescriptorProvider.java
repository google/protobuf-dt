/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.parser.IParser;

/**
 * Provider of a singleton instance of <code>{@link ProtoDescriptor}</code>.
 *
 * @author Alex Ruiz
 */
@Singleton
public class ProtoDescriptorProvider implements Provider<ProtoDescriptor> {

  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.descriptorSource";

  @Inject private IParser parser;
  @Inject private ModelNodes nodes;
  @Inject private IExtensionRegistry registry;

  private ProtoDescriptor descriptor;

  private final Object lock = new Object();

  public ProtoDescriptor get() {
    synchronized (lock) {
      if (descriptor == null) {
        descriptor = new ProtoDescriptor(parser, descriptorLocation(), nodes);
      }
      return descriptor;
    }
  }
  
  private URI descriptorLocation() {
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    for (IConfigurationElement e : config) {
      String path = e.getAttribute("path");
      if (isEmpty(path)) continue;
      return URI.createURI("platform:/plugin/" + e.getContributor().getName() + "/" + path);
    }
    return URI.createURI("platform:/plugin/com.google.eclipse.protobuf/descriptor.proto");
  }
}

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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.parser.IParser;

import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.*;

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
  private URI descriptorLocation;

  private final Object lock = new Object();

  public ProtoDescriptor get() {
    synchronized (lock) {
      if (descriptor == null) {
        descriptor = new ProtoDescriptor(parser, descriptorLocation(), nodes);
      }
    }
    return descriptor;
  }

  public URI descriptorLocation() {
    synchronized (lock) {
      if (descriptorLocation == null) descriptorLocation = findDescriptorLocation();
    }
    return descriptorLocation;
  }

  private URI findDescriptorLocation() {
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    if (config == null) return defaultDescriptor();
    for (IConfigurationElement e : config) {
      String path = e.getAttribute("path");
      if (isEmpty(path)) continue;
      StringBuilder uri = new StringBuilder();
      uri.append("platform:/plugin/").append(e.getContributor().getName()).append("/").append(path);
      return URI.createURI(uri.toString());
    }
    return defaultDescriptor();
  }
  
  private static URI defaultDescriptor() {
    return URI.createURI("platform:/plugin/com.google.eclipse.protobuf/descriptor.proto");
  }
}

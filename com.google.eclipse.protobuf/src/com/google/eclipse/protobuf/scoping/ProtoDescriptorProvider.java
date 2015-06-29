/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.DESCRIPTOR_PROTO_PATH;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.preferences.general.GeneralPreferences;
import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provider of <code>{@link ProtoDescriptor}</code>s.
 *
 * @author Alex Ruiz
 */
@Singleton public class ProtoDescriptorProvider {
  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.descriptorSource";

  private final IPreferenceStoreAccess storeAccess;
  private final IExtensionRegistry registry;
  private final IParser parser;
  private final INodes nodes;
  private final IUriResolver resolver;

  private static final URI DEFAULT_DESCRIPTOR_LOCATION =
      URI.createURI("platform:/plugin/com.google.eclipse.protobuf/descriptor.proto");
  private final ProtoDescriptorInfo openSourceProtoDescriptorInfo;
  private final ProtoDescriptorInfo extensionPointDescriptorInfo;

  private static final String MAP_ENTRY_DESCRIPTOR_PATH = "google/protobuf/map_entry.proto";
  private static final URI MAP_ENTRY_DESCRIPTOR_LOCATION =
      URI.createURI("platform:/plugin/com.google.eclipse.protobuf/map_entry.proto");;
  private final ProtoDescriptorInfo mapEntryDescriptorInfo;

  private static final Logger LOG =
      Logger.getLogger(ProtoDescriptorProvider.class.getCanonicalName());

  private final
      LoadingCache<IProject, Map<String, ProtoDescriptorInfo>> descriptorCache = CacheBuilder
          .newBuilder().build(new CacheLoader<IProject, Map<String, ProtoDescriptorInfo>>() {
        @Override
        public Map<String, ProtoDescriptorInfo> load(final IProject project) {
          return loadDescriptorInfos(project);
        }
      });

  @Inject
  ProtoDescriptorProvider(IPreferenceStoreAccess storeAccess, IExtensionRegistry registry,
      IParser parser, INodes nodes, IUriResolver resolver) {
    this.storeAccess = storeAccess;
    this.registry = registry;
    this.parser = parser;
    this.nodes = nodes;
    this.resolver = resolver;
    this.openSourceProtoDescriptorInfo = getOpenSourceProtoDescriptorInfo();
    this.mapEntryDescriptorInfo = getMapEntryDescriptorInfo();
    this.extensionPointDescriptorInfo = getExtensionPointDescriptorInfo();
  }

  public ProtoDescriptor primaryDescriptor(IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    for (ProtoDescriptorInfo descriptorInfo : descriptorInfos.values()) {
      return descriptorInfo.protoDescriptor;
    }
    return openSourceProtoDescriptorInfo.protoDescriptor;
  }

  public ProtoDescriptor descriptor(IProject project, String importUri) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    ProtoDescriptorInfo descriptorInfo = descriptorInfos.get(importUri);
    if (descriptorInfo != null) {
      return descriptorInfo.protoDescriptor;
    }
    URI uri = URI.createURI(importUri);
    for (Entry<String, ProtoDescriptorInfo> info : descriptorInfos.entrySet()) {
      if (info.getValue().location.equals(uri)) {
        return descriptor(project, info.getKey());
      }
    }
    return null;
  }

  public ImmutableList<URI> allDescriptorLocations(IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    ImmutableList.Builder<URI> descriptorLocations = ImmutableList.builder();
    for (ProtoDescriptorInfo descriptorInfo : descriptorInfos.values()) {
      descriptorLocations.add(descriptorInfo.location);
    }
    return descriptorLocations.build();
  }

  public URI descriptorLocation(IProject project, String importUri) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    for (ProtoDescriptorInfo descriptorInfo : descriptorInfos.values()) {
      if (descriptorInfo.importUri.equals(importUri)) {
        return descriptorInfo.location;
      }
    }
    return null;
  }

  public ProtoDescriptor mapEntryDescriptor() {
    return mapEntryDescriptorInfo.protoDescriptor;
  }

  private Map<String, ProtoDescriptorInfo> loadDescriptorInfos(final IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos =
        new LinkedHashMap<String, ProtoDescriptorProvider.ProtoDescriptorInfo>();

    // Add descriptor.proto from preferences
    GeneralPreferences preferences = new GeneralPreferences(storeAccess, project);
    String descriptorProtoUri = preferences.getDescriptorProtoPath();
    if (!PreferenceNames.DEFAULT_DESCRIPTOR_PATH.equals(descriptorProtoUri)) {
      String resolvedUri = resolver.resolveUri(descriptorProtoUri, null, project);
      if (resolvedUri != null) {
        URI descriptorProtoLocation = URI.createURI(resolvedUri);
        if (descriptorProtoLocation != null) {
          ProtoDescriptor protoDescriptor =
              new ProtoDescriptor(descriptorProtoUri, descriptorProtoLocation, parser, nodes);
          ProtoDescriptorInfo descriptorInfo =
              new ProtoDescriptorInfo(descriptorProtoUri, descriptorProtoLocation, protoDescriptor);
          descriptorInfos.put(descriptorProtoUri, descriptorInfo);
        }
      } else {
        LOG.log(Level.WARNING,
            "Unable to resolve URI for descriptor proto location: " + descriptorProtoUri);
      }
    }

    // Add the extension point descriptor proto
    if (extensionPointDescriptorInfo != null) {
      if (!descriptorInfos.containsKey(extensionPointDescriptorInfo.importUri)) {
        descriptorInfos.put(extensionPointDescriptorInfo.importUri, extensionPointDescriptorInfo);
      }
    }

    // Add the open source descriptor proto
    if (!descriptorInfos.containsKey(PreferenceNames.DEFAULT_DESCRIPTOR_PATH)) {
      descriptorInfos.put(PreferenceNames.DEFAULT_DESCRIPTOR_PATH,
          openSourceProtoDescriptorInfo);
    }

    // Set property change listener for this project
    storeAccess.getContextPreferenceStore(project).addPropertyChangeListener(
        new IPropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty().contains(DESCRIPTOR_PROTO_PATH)) {
              descriptorCache.invalidate(project);
            }
          }
        });
    return descriptorInfos;
  }

  private Map<String, ProtoDescriptorInfo> getDescriptorInfosFor(IProject project) {
    if (project == null) {
      Map<String, ProtoDescriptorInfo> descriptorInfos =
          new LinkedHashMap<>();
      descriptorInfos.put(PreferenceNames.DEFAULT_DESCRIPTOR_PATH, openSourceProtoDescriptorInfo);
      return descriptorInfos;
    }
    try {
      return descriptorCache.get(project);
    } catch (ExecutionException e) {
      LOG.log(Level.SEVERE, "Error while trying to determine descriptor.proto for project", e);
      return null;
    }
  }

  private ProtoDescriptorInfo getOpenSourceProtoDescriptorInfo() {
    ProtoDescriptor descriptor = new ProtoDescriptor(PreferenceNames.DEFAULT_DESCRIPTOR_PATH,
        DEFAULT_DESCRIPTOR_LOCATION, parser, nodes);
    return new ProtoDescriptorInfo(PreferenceNames.DEFAULT_DESCRIPTOR_PATH,
        DEFAULT_DESCRIPTOR_LOCATION, descriptor);
  }

  private ProtoDescriptorInfo getMapEntryDescriptorInfo() {
    ProtoDescriptor descriptor = new ProtoDescriptor(
        MAP_ENTRY_DESCRIPTOR_PATH, MAP_ENTRY_DESCRIPTOR_LOCATION, parser, nodes);
    return new ProtoDescriptorInfo(
        MAP_ENTRY_DESCRIPTOR_PATH, MAP_ENTRY_DESCRIPTOR_LOCATION, descriptor);
  }

  private ProtoDescriptorInfo getExtensionPointDescriptorInfo() {
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    if (config == null) {
      return null;
    }
    for (IConfigurationElement e : config) {
      ProtoDescriptorInfo info = descriptorInfo(e);
      if (info != null) {
        return info;
      }
    }
    return null;
  }

  private ProtoDescriptorInfo descriptorInfo(IConfigurationElement e) {
    String importUri = e.getAttribute("importUri");
    if (importUri.isEmpty()) {
      return null;
    }
    URI location = descriptorLocation(e);
    if (location == null) {
      return null;
    }
    try {
      return new ProtoDescriptorInfo(importUri, location,
          new ProtoDescriptor(importUri, location, parser, nodes));
    } catch (IllegalStateException exception) {
      LOG.log(Level.WARNING, "Error when initializing descriptor proto from extension point",
          exception);
      return null;
    }
  }

  private static URI descriptorLocation(IConfigurationElement e) {
    String path = e.getAttribute("path");
    if (path.isEmpty()) {
      return null;
    }
    StringBuilder uri = new StringBuilder();
    uri.append("platform:/plugin/").append(e.getContributor().getName()).append("/").append(path);
    return URI.createURI(uri.toString());
  }

  private static class ProtoDescriptorInfo {
    final String importUri;
    final URI location;
    final ProtoDescriptor protoDescriptor;

    ProtoDescriptorInfo(String importUri, URI location, ProtoDescriptor protoDescriptor) {
      this.importUri = importUri;
      this.location = location;
      this.protoDescriptor = protoDescriptor;
    }
  }
}

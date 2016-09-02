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
import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.preferences.general.GeneralPreferences;
import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provider of <code>{@link ProtoDescriptor}</code>s.
 *
 * @author Alex Ruiz
 */
@Singleton
public class ProtoDescriptorProvider {
  public class ProtoDescriptorInfo {
    final String importUri;
    final URI location;

    ProtoDescriptorInfo(String importUri, URI location) {
      this.importUri = importUri;
      this.location = location;
    }
  }

  private static final URI DEFAULT_DESCRIPTOR_LOCATION =
      URI.createURI("platform:/plugin/com.google.eclipse.protobuf/descriptor.proto");
  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.descriptorSource";
  private static final Logger LOG =
      Logger.getLogger(ProtoDescriptorProvider.class.getCanonicalName());

  private static URI descriptorLocation(IConfigurationElement e) {
    String path = e.getAttribute("path");
    if (path.isEmpty()) {
      return null;
    }
    StringBuilder uri = new StringBuilder();
    uri.append("platform:/plugin/").append(e.getContributor().getName()).append("/").append(path);
    return URI.createURI(uri.toString());
  }

  private final LoadingCache<IProject, Map<String, ProtoDescriptorInfo>> descriptorCache =
      CacheBuilder.newBuilder()
          .build(
              new CacheLoader<IProject, Map<String, ProtoDescriptorInfo>>() {
                @Override
                public Map<String, ProtoDescriptorInfo> load(final IProject project) {
                  return loadDescriptorInfos(project);
                }
              });

  private final ProtoDescriptorInfo extensionPointDescriptorInfo;
  private final ProtoDescriptorInfo openSourceProtoDescriptorInfo;
  private final IExtensionRegistry registry;

  private final IUriResolver resolver;

  private final IPreferenceStoreAccess storeAccess;

  @Inject
  ProtoDescriptorProvider(
      IPreferenceStoreAccess storeAccess, IExtensionRegistry registry, IUriResolver resolver) {
    this.storeAccess = storeAccess;
    this.registry = registry;
    this.resolver = resolver;
    this.openSourceProtoDescriptorInfo = getOpenSourceProtoDescriptorInfo();
    this.extensionPointDescriptorInfo = getExtensionPointDescriptorInfo();
  }

  public ImmutableList<URI> allDescriptorLocations(IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    ImmutableList.Builder<URI> descriptorLocations = ImmutableList.builder();
    for (ProtoDescriptorInfo descriptorInfo : descriptorInfos.values()) {
      descriptorLocations.add(descriptorInfo.location);
    }
    return descriptorLocations.build();
  }

  public ProtoDescriptorInfo descriptor(IProject project, String importUri) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    ProtoDescriptorInfo descriptorInfo = descriptorInfos.get(importUri);
    if (descriptorInfo != null) {
      return descriptorInfo;
    }
    URI uri = URI.createURI(importUri);
    for (Entry<String, ProtoDescriptorInfo> info : descriptorInfos.entrySet()) {
      if (info.getValue().location.equals(uri)) {
        return descriptor(project, info.getKey());
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
      return new ProtoDescriptorInfo(importUri, location);
    } catch (IllegalStateException exception) {
      LOG.log(
          Level.WARNING,
          "Error when initializing descriptor proto from extension point",
          exception);
      return null;
    }
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

  private Map<String, ProtoDescriptorInfo> getDescriptorInfosFor(IProject project) {
    if (project == null) {
      Map<String, ProtoDescriptorInfo> descriptorInfos = new LinkedHashMap<>();
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

  private ProtoDescriptorInfo getOpenSourceProtoDescriptorInfo() {
    return new ProtoDescriptorInfo(
        PreferenceNames.DEFAULT_DESCRIPTOR_PATH, DEFAULT_DESCRIPTOR_LOCATION);
  }

  private Map<String, ProtoDescriptorInfo> loadDescriptorInfos(final IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = new LinkedHashMap<>();

    // Add descriptor.proto from preferences
    GeneralPreferences preferences = new GeneralPreferences(storeAccess, project);
    String descriptorProtoUri = preferences.getDescriptorProtoPath();
    if (!PreferenceNames.DEFAULT_DESCRIPTOR_PATH.equals(descriptorProtoUri)) {
      String resolvedUri = resolver.resolveUri(descriptorProtoUri, null, project);
      if (resolvedUri != null) {
        URI descriptorProtoLocation = URI.createURI(resolvedUri);
        if (descriptorProtoLocation != null) {
          ProtoDescriptorInfo descriptorInfo =
              new ProtoDescriptorInfo(descriptorProtoUri, descriptorProtoLocation);
          descriptorInfos.put(descriptorProtoUri, descriptorInfo);
        }
      } else {
        LOG.log(
            Level.WARNING,
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
      descriptorInfos.put(PreferenceNames.DEFAULT_DESCRIPTOR_PATH, openSourceProtoDescriptorInfo);
    }

    IPropertyChangeListener changeListener =
        new IPropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent event) {
            String property = event.getProperty();
            if (property.contains(DESCRIPTOR_PROTO_PATH)
                || property.contains(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME)) {
              descriptorCache.invalidate(project);
            }
          }
        };

    // Set property change listener for this project
    preferences.addPropertyChangeListener(changeListener);
    return descriptorInfos;
  }

  public ProtoDescriptorInfo primaryDescriptor(IProject project) {
    Map<String, ProtoDescriptorInfo> descriptorInfos = getDescriptorInfosFor(project);
    for (ProtoDescriptorInfo descriptorInfo : descriptorInfos.values()) {
      return descriptorInfo;
    }
    return openSourceProtoDescriptorInfo;
  }
}

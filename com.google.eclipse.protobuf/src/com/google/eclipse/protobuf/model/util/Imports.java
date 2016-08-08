/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.StringLiteral;
import com.google.eclipse.protobuf.resource.ResourceSets;
import com.google.eclipse.protobuf.scoping.IImportResolver;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.util.EResources;
import com.google.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility methods related to imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Imports {
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private ResourceSets resourceSets;
  @Inject private StringLiterals stringLiterals;
  @Inject private IImportResolver importResolver;

  /**
   * Indicates whether the URI of the given {@code Import} is equal to the path of the file
   * "descriptor.proto."
   *
   * @param anImport the {@code Import} to check.
   * @return {@code true} if the URI of the given {@code Import} is equal to the path of the file
   *     "descriptor.proto," {@code false} otherwise.
   */
  public boolean hasUnresolvedDescriptorUri(Import anImport) {
    if (anImport == null) {
      return false;
    }
    IProject project = EResources.getProjectOf(anImport.eResource());
    URI descriptorLocation = descriptorProvider.descriptorLocation(project, getPath(anImport));
    return descriptorLocation != null;
  }

  /**
   * Indicates whether the given {@code Import} is pointing to descriptor.proto.
   *
   * @param anImport the given {@code Import} to check.
   * @return {@code true} if the given {@code Import} is pointing to descriptor.proto, {@code false}
   *     otherwise.
   */
  public boolean isImportingDescriptor(Import anImport) {
    if (hasUnresolvedDescriptorUri(anImport)) {
      return true;
    }
    if (anImport == null) {
      return false;
    }
    String importUri = getPath(anImport);
    IProject project = EResources.getProjectOf(anImport.eResource());
    for (URI locationUri : descriptorProvider.allDescriptorLocations(project)) {
      String location = locationUri.toString();
      if (location.equals(importUri)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Indicates whether the URI of the given {@code Import} can be resolved.
   *
   * @param anImport the given {@code Import}.
   * @return {@code true} if the URI of the given {@code Import} can be resolved, {@code false}
   *     otherwise.
   */
  public boolean isResolved(Import anImport) {
    return resolvedUriOf(anImport) != null;
  }

  /**
   * Returns the resource referred by the URI of the given {@code Import}.
   *
   * @param anImport the given {@code Import}.
   * @return the resource referred by the URI of the given {@code Import}, or {@code null} if the
   *     URI has not been resolved.
   */
  public Resource importedResource(Import anImport) {
    URI resolvedUri = resolvedUriOf(anImport);
    if (resolvedUri != null) {
      ResourceSet resourceSet = anImport.eResource().getResourceSet();
      return resourceSets.findResource(resourceSet, resolvedUri);
    }
    return null;
  }

  /**
   * Returns the resolved URI of the given {@code Import}.
   *
   * @param anImport the the given {@code Import}.
   * @return the resolved URI of the given {@code Import}, or {@code null} if the URI was not
   *     successfully resolved.
   */
  public URI resolvedUriOf(Import anImport) {
    String resolvedUri = importResolver.resolve(anImport);
    if (isNullOrEmpty(resolvedUri)) {
      return null;
    }
    URI uri = URI.createURI(resolvedUri);
    return (isResolved(uri)) ? uri : null;
  }

  private boolean isResolved(URI uri) {
    return !isEmpty(uri.scheme());
  }

  /**
   * Returns the path that is being imported by the given {@link Import} as a {@code String} or null
   * if the {@code anImport.getPath()} is null.
   */
  public @Nullable String getPath(Import anImport) {
    StringLiteral path = anImport.getPath();
    if (path != null) {
      return stringLiterals.getCombinedString(path);
    }
    return null;
  }
}

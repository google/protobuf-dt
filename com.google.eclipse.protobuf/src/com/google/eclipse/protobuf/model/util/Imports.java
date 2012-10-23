/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.conversion.STRINGValueConverter;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.resource.ResourceSets;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.inject.Inject;

/**
 * Utility methods related to imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Imports {
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private INodes nodes;
  @Inject private ResourceSets resourceSets;
  @Inject private STRINGValueConverter converter;
  @Inject private ImportUriResolver uriResolver;

  /**
   * Indicates whether the URI of the given {@code Import} is equal to the path of the file "descriptor.proto."
   * @param anImport the {@code Import} to check.
   * @return {@code true} if the URI of the given {@code Import} is equal to the path of the file "descriptor.proto,"
   * {@code false}
   * otherwise.
   */
  public boolean hasUnresolvedDescriptorUri(Import anImport) {
    if (anImport == null) {
      return false;
    }
    URI descriptorLocation = descriptorProvider.descriptorLocation(anImport.getImportURI());
    return descriptorLocation != null;
  }

  /**
   * Indicates whether the given {@code Import} is pointing to descriptor.proto.
   * @param anImport the given {@code Import} to check.
   * @return {@code true} if the given {@code Import} is pointing to descriptor.proto, {@code false} otherwise.
   */
  public boolean isImportingDescriptor(Import anImport) {
    if (hasUnresolvedDescriptorUri(anImport)) {
      return true;
    }
    if (anImport == null) {
      return false;
    }
    String importUri = anImport.getImportURI();
    for (URI locationUri : descriptorProvider.allDescriptorLocations()) {
      String location = locationUri.toString();
      if (location.equals(importUri)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the URI of the given {@code Import} as it looks in the editor (i.e. before it is resolved.)
   * @param anImport the given {@code Import}.
   * @return the URI of the given {@code Import} as it looks in the editor.
   */
  public String uriAsEnteredByUser(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    String text = (node == null) ? null : node.getText();
    if (text == null) {
      return null;
    }
    return converter.toValue(text, node);
  }

  /**
   * Indicates whether the URI of the given {@code Import} has been resolved.
   * @param anImport the given {@code Import}.
   * @return {@code true} if the URI of the given {@code Import} has been resolved, {@code false} otherwise.
   */
  public boolean isResolved(Import anImport) {
    String uriAsText = anImport.getImportURI();
    if (!isEmpty(uriAsText)) {
      URI uri = URI.createURI(uriAsText);
      return isResolved(uri);
    }
    return false;
  }

  /**
   * Returns the resource referred by the URI of the given {@code Import}.
   * @param anImport the given {@code Import}.
   * @return the resource referred by the URI of the given {@code Import}, or {@code null} if the URI has not been
   * resolved.
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
   * @param anImport the the given {@code Import}.
   * @return the resolved URI of the given {@code Import}, or {@code null} if the URI was not successfully resolved.
   */
  public URI resolvedUriOf(Import anImport) {
    String resolvedUri = uriResolver.apply(anImport);
    if (isEmpty(resolvedUri)) {
      return null;
    }
    URI uri = URI.createURI(resolvedUri);
    return (isResolved(uri)) ? uri : null;
  }

  private boolean isResolved(URI uri) {
    return !isEmpty(uri.scheme());
  }
}

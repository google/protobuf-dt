/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IResourceDescription;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.cdt.matching.ProtobufElementMatcher;
import com.google.eclipse.protobuf.resource.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufElementUriFinder {
  @Inject private ProtobufElementMatcher matcher;
  @Inject private ResourceDescriptions descriptions;
  @Inject private IndexLookup indexLookup;
  @Inject private AstBasedCppToProtobufMapper mapper;
  @Inject private ProtoFilePathFinder pathFinder;

  URI findProtobufElementUriFromSelectionOf(IEditorPart editor) {
    IPath protoFilePath = pathFinder.findProtoFilePathIn(editor);
    if (protoFilePath != null) {
      CppToProtobufMapping mapping = mapper.createMappingFromSelectionOf(editor);
      if (mapping != null) {
        IResourceDescription resource = indexLookup.resourceIn(protoFilePath);
        return findProtobufElementUri(resource, mapping);
      }
    }
    return null;
  }

  private URI findProtobufElementUri(IResourceDescription resource, CppToProtobufMapping mapping) {
    if (resource == null) {
      return null;
    }
    // try first direct lookup.
    QualifiedName qualifiedName = mapping.qualifiedName();
    URI foundUri = descriptions.modelObjectUri(resource, qualifiedName);
    if (foundUri != null) {
      return foundUri;
    }
    // try finding the best match.
    return matcher.findUriOfMatchingProtobufElement(resource, mapping);
  }
}

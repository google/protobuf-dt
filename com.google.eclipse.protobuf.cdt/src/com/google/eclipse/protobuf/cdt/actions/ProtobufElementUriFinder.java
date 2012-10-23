/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.cdt.matching.ProtobufElementMatchFinder;
import com.google.eclipse.protobuf.cdt.util.Editors;
import com.google.eclipse.protobuf.resource.IndexLookup;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufElementUriFinder {
  @Inject private Editors editors;
  @Inject private IResourceSetProvider resourceSetProvider;
  @Inject private ProtobufElementMatchFinder matcher;
  @Inject private IndexLookup indexLookup;
  @Inject private AstBasedCppToProtobufMapper mapper;
  @Inject private ProtoFilePathFinder pathFinder;

  URI findProtobufElementLocationFromSelectionOf(IEditorPart editor) {
    IPath protoFilePath = pathFinder.findProtoFilePathIn(editor);
    if (protoFilePath != null) {
      CppToProtobufMapping mapping = mapper.createMappingFromSelectionOf(editor);
      if (mapping != null) {
        return protobufElementLocation(protoFilePath, editor, mapping);
      }
    }
    return null;
  }

  private URI protobufElementLocation(IPath protoFilePath, IEditorPart editor, CppToProtobufMapping mapping) {
    Resource resource = protoFileResource(protoFilePath, editor);
    if (resource == null) {
      return null;
    }
    List<URI> locations = matcher.matchingProtobufElementLocations(resource, mapping);
    return (!locations.isEmpty()) ? locations.get(0) : null;
  }

  private Resource protoFileResource(IPath protoFilePath, IEditorPart editor) {
    IFile cppHeaderFile = editors.fileDisplayedIn(editor);
    ResourceSet resourceSet = resourceSetProvider.get(cppHeaderFile.getProject());
    if (resourceSet != null) {
      IResourceDescription description = indexLookup.resourceIn(protoFilePath);
      return (description != null) ? resourceSet.getResource(description.getURI(), true) : null;
    }
    return null;
  }
}

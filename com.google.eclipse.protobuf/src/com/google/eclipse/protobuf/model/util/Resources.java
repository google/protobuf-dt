/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;

import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Resource}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Resources {
  /**
   * Returns the root element of the given resource.
   * @param resource the given resource.
   * @return the root element of the given resource, or {@code null} if the given resource does not have a root element.
   */
  public Protobuf rootOf(Resource resource) {
    if (resource instanceof XtextResource) {
      IParseResult parseResult = ((XtextResource) resource).getParseResult();
      if (parseResult != null) {
        EObject root = parseResult.getRootASTElement();
        return (Protobuf) root;
      }
    }
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof Protobuf) {
        return (Protobuf) next;
      }
    }
    return null;
  }
}

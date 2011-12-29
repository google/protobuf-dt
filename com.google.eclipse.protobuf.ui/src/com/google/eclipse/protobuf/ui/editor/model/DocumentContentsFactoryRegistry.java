/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import java.util.*;

import org.eclipse.ui.IEditorInput;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class DocumentContentsFactoryRegistry {
  private final List<DocumentContentsFactory> factories = new ArrayList<DocumentContentsFactory>();

  @Inject
  DocumentContentsFactoryRegistry(FileStoreDocumentContentsFactory factory1, UriDocumentContentsFactory factory2) {
    factories.add(factory1);
    factories.add(factory2);
  }

  DocumentContentsFactory findFactory(Object element) {
    if (!(element instanceof IEditorInput)) {
      return null;
    }
    IEditorInput input = (IEditorInput) element;
    for (DocumentContentsFactory factory : factories) {
      if (factory.supportsEditorInputType(input)) {
        return factory;
      }
    }
    return null;
  }
}

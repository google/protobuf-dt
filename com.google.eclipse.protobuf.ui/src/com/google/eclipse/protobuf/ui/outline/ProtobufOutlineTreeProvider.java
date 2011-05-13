/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * Customization of the default outline structure.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufOutlineTreeProvider extends DefaultOutlineTreeProvider {

  boolean _isLeaf(Option o) {
    return true;
  }

  boolean _isLeaf(Property p) {
    return true;
  }

  protected void _createChildren(DocumentRootNode parentNode, Protobuf protobuf) {
    Package aPackage = protobuf.getPackage();
    if (aPackage != null) {
      createNode(parentNode, aPackage);
    }
    if (!protobuf.getImports().isEmpty()) {
      createEStructuralFeatureNode(parentNode, protobuf, PROTOBUF__IMPORTS,
          labelProvider.getImage("imports"), "import declarations", false);
    }
    if (!protobuf.getOptions().isEmpty()) {
      createEStructuralFeatureNode(parentNode, protobuf, PROTOBUF__OPTIONS,
          labelProvider.getImage("options"), "option declarations", false);
    }
    for (ProtobufElement e : protobuf.getElements()) {
      createNode(parentNode, e);
    }
  }

  @Override protected void createNode(IOutlineNode parent, EObject modelElement) {
    if (modelElement instanceof MessageReference) return;
    super.createNode(parent, modelElement);
  }
}

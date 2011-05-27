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
import static com.google.eclipse.protobuf.ui.outline.Messages.*;

import java.util.ArrayList;
import java.util.List;

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

  private static final List<Class<? extends EObject>> IGNORED_ELEMENT_TYPES = new ArrayList<Class<? extends EObject>>();
  
  static {
    IGNORED_ELEMENT_TYPES.add(BooleanRef.class);
    IGNORED_ELEMENT_TYPES.add(MessageReference.class);
  }
  
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
          labelProvider.getImage("imports"), importDeclarations, false);
    }
    if (!protobuf.getOptions().isEmpty()) {
      createEStructuralFeatureNode(parentNode, protobuf, PROTOBUF__OPTIONS,
          labelProvider.getImage("options"), optionDeclarations, false);
    }
    for (ProtobufElement e : protobuf.getElements()) {
      createNode(parentNode, e);
    }
  }

  @Override protected void createNode(IOutlineNode parent, EObject modelElement) {
    if (isIgnored(modelElement)) return;
    super.createNode(parent, modelElement);
  }
  
  private boolean isIgnored(EObject modelElement) {
    for (Class<? extends EObject> ignoredType : IGNORED_ELEMENT_TYPES)
      if (ignoredType.isInstance(modelElement)) return true;
    return false;
  }
}

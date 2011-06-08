/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findNodesForFeature;

import com.google.inject.Singleton;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.INode;

import java.util.List;

/**
 * Utility methods related to <code>{@link INode}</code>s.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class ModelNodes {

  public INode firstNodeForFeature(EObject semanticObject, EStructuralFeature structuralFeature) {
    List<INode> nodes = findNodesForFeature(semanticObject, structuralFeature);
    if (nodes.isEmpty()) return null;
    return nodes.get(0);
  }
}

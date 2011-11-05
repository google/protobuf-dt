/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.impl.AbstractNode;

import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class Finder {

  private static final String[] FEATURE_NAMES = { "name", "message", "type", "source", "optionField" };
  
  private final String protoAsText;
  private final AbstractNode root;

  Finder(INode node, String protoAsText) {
    this.root = rootOf(node);
    this.protoAsText = protoAsText;
  }
  
  private static AbstractNode rootOf(INode node) {
    while (!(node instanceof AbstractNode)) {
      node = node.getParent();
    }
    return (AbstractNode) node;
  }
  
  <T extends EObject> T find(String text, int count, Class<T> type) {
    int offset = protoAsText.indexOf(text);
    String name = text.substring(0, count);
    BidiTreeIterator<AbstractNode> iterator = root.basicIterator();
    while (iterator.hasNext()) {
      AbstractNode node = iterator.next();
      int nodeOffset = node.getOffset();
      if (nodeOffset > offset || (nodeOffset + node.getLength()) <= offset) continue;
      EObject e = node.getSemanticElement();
      if (isDefaultValueFieldOption(name, type, e)) {
        return type.cast(e);
      }
      if (type.isInstance(e) && name.equals(nameOf(e))) {
        return type.cast(e);
      }
    }
    String format = "Unable to find element. Text: '%s', count: %d, type: %s";
    throw new AssertionError(String.format(format, text, count, type.getName()));
  }
  
  private boolean isDefaultValueFieldOption(String name, Class<?> type, EObject element) {
    return "default".equals(name) && type.isInstance(element) && element instanceof DefaultValueFieldOption;
  }
  
  private String nameOf(Object o) {
    if (!(o instanceof EObject)) return null;
    EObject e = (EObject) o;
    for (String name : FEATURE_NAMES) {
      Object value = feature(e, name);
      if (value instanceof String) return (String) value;
      if (value != null) return nameOf(value);
    }
    return null;
  }
  
  private Object feature(EObject e, String featureName) {
    EStructuralFeature f = e.eClass().getEStructuralFeature(featureName);
    return (f != null) ? e.eGet(f) : null;
  }

  ILeafNode find(String text) {
    BidiTreeIterator<AbstractNode> iterator = root.basicIterator();
    while (iterator.hasNext()) {
      AbstractNode node = iterator.next();
      if (!(node instanceof ILeafNode)) continue;
      String nodeText = clean(node.getText());
      if (text.equals(nodeText)) return (ILeafNode) node;
    }
    return null;
  }
  
  private String clean(String text) {
    return text.replace(lineSeparator(), " ").trim();
  }
}

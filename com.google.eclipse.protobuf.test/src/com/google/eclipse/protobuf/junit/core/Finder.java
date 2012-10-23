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

import static com.google.eclipse.protobuf.junit.core.SearchOption.IGNORE_CASE;
import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.AbstractNode;

import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class Finder {
  // TODO remove feature names that are not used or don't exist any more.
  private static final String[] FEATURE_NAMES = { "extension", "target", "message", "name", "optionField", "property",
      "source", "type", "value" };

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

  <T extends EObject> T find(String text, int count, Class<T> type, List<SearchOption> options) {
    int offset = protoAsText.indexOf(text);
    String name = text.substring(0, count);
    Iterator<AbstractNode> iterator = root.basicIterator();
    while (iterator.hasNext()) {
      AbstractNode node = iterator.next();
      int nodeOffset = node.getOffset();
      if (nodeOffset > offset || (nodeOffset + node.getLength()) <= offset) {
        continue;
      }
      EObject e = node.getSemanticElement();
      if (isDefaultValueFieldOption(name, type, e)) {
        return type.cast(e);
      }
      if (type.isInstance(e)) {
        if (areNamesEqual(name, e, options)) {
          return type.cast(e);
        }
      }
    }
    String format = "Unable to find element. Text: '%s', count: %d, type: %s, options: %s";
    throw new AssertionError(String.format(format, text, count, type.getName(), options));
  }

  private boolean isDefaultValueFieldOption(String name, Class<?> type, EObject element) {
    return "default".equals(name) && type.isInstance(element) && element instanceof DefaultValueFieldOption;
  }

  private boolean areNamesEqual(String expected, Object e, List<SearchOption> options) {
    String realExpected = expected;
    if (realExpected.indexOf(".") != -1 && !(e instanceof Package)) {
      String[] segments = expected.split("\\.");
      QualifiedName qualifiedName = QualifiedName.create(segments);
      realExpected = qualifiedName.getLastSegment();
    }
    String actual = nameOf(e);
    if (options.contains(IGNORE_CASE)) {
      return realExpected.equalsIgnoreCase(actual);
    }
    return realExpected.equals(actual);
  }

  private String nameOf(Object o) {
    if (!(o instanceof EObject)) {
      return null;
    }
    EObject e = (EObject) o;
    for (String name : FEATURE_NAMES) {
      Object value = feature(e, name);
      if (value instanceof String) {
        return (String) value;
      }
      if (value != null) {
        return nameOf(value);
      }
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
      if (!(node instanceof ILeafNode)) {
        continue;
      }
      String nodeText = clean(node.getText());
      if (text.equals(nodeText)) {
        return (ILeafNode) node;
      }
    }
    String format = "Unable to find node. Text: '%s'";
    throw new AssertionError(String.format(format, text));
  }

  private String clean(String text) {
    return text.replace(lineSeparator(), " ").trim();
  }
}

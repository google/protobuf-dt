/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import static java.util.Collections.singletonList;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;

import com.google.common.base.Predicate;

/**
 * Outline Page for Protocol Buffer editors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufOutlinePage extends OutlinePage {
  @Override protected List<IOutlineNode> getInitiallyExpandedNodes() {
    IOutlineNode rootNode = getTreeProvider().createRoot(getXtextDocument());
    List<IOutlineNode> nodes = newArrayList(rootNode);
    addChildrenToExpand(singletonList(rootNode), nodes, 0);
    return nodes;
  }

  private void addChildrenToExpand(Collection<IOutlineNode> parents, List<IOutlineNode> nodes, int depth) {
    if (depth < 1) {
      return;
    }
    for (IOutlineNode parent : parents) {
      Collection<IOutlineNode> children = childrenToExpand(parent);
      nodes.addAll(children);
      addChildrenToExpand(children, nodes, depth - 1);
    }
  }

  private Collection<IOutlineNode> childrenToExpand(IOutlineNode parent) {
    if (parent instanceof DocumentRootNode) {
      return filter(parent.getChildren(), new Predicate<IOutlineNode>() {
        @Override public boolean apply(IOutlineNode node) {
          return !(node instanceof SimpleOutlineNode);
        }
      });
    }
    return parent.getChildren();
  }
}

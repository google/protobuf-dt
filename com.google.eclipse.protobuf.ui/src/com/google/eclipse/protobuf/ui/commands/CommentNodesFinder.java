/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.junit.util.SystemProperties.lineSeparator;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.*;

import java.util.regex.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class CommentNodesFinder {

  @Inject private ModelNodes nodes;

  INode matchingCommentNode(EObject target, Pattern...patternsToMatch) {
    ICompositeNode node = getNode(target);
    for (INode currentNode : node.getAsTreeIterable()) {
      if (currentNode instanceof ILeafNode && !((ILeafNode) currentNode).isHidden()) break;
      if (currentNode instanceof ILeafNode && nodes.wasCreatedByAnyComment(currentNode)) {
        String rawComment = ((ILeafNode) currentNode).getText();
        if (isEmpty(rawComment)) continue;
        String[] comment = rawComment.split(lineSeparator());
        for (String line : comment) {
          for (Pattern pattern : patternsToMatch) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) return currentNode;
          }
        }
      }
    }
    return null;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.*;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.inject.Inject;

/**
 * Provides single line comments of a protobuf element as its documentation when hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class MLCommentDocumentationProvider implements IEObjectDocumentationProvider {

  private static final String START_TAG = "/\\*\\*?";
  private static final String END_TAG = "\\*/";
  private static final String LINE_PREFIX = "\\** ?";
  private static final String LINE_POSTFIX = "\\**";
  private static final String WHITE_SPACE = "( |\\t)*";
  
  @Inject private INodes nodes;

  @Override public String getDocumentation(EObject o) {
    String comment = findComment(o);
    return comment != null ? comment : "";
  }

  private String findComment(EObject o) {
    String returnValue = null;
    ICompositeNode node = getNode(o);
    if (node == null) return null;
    // get the last multi line comment before a non hidden leaf node
    for (INode currentNode : node.getAsTreeIterable()) {
      if (currentNode instanceof ILeafNode && !((ILeafNode) currentNode).isHidden()) break;
      if (currentNode instanceof ILeafNode && nodes.belongsToMultipleLineComment(currentNode)) {
        String comment = ((ILeafNode) currentNode).getText();
        if (comment.matches("(?s)" + START_TAG + ".*")) {
          returnValue = cleanUp(comment);
        }
      }
    }
    return returnValue;
  }
  
  private String cleanUp(String comment) {
    return comment.replaceAll("\\A" + START_TAG, "")
                  .replaceAll(END_TAG + "\\z", "")
                  .replaceAll("(?m)^"+ WHITE_SPACE + LINE_PREFIX, "")
                  .replaceAll("(?m)" + WHITE_SPACE + LINE_POSTFIX + WHITE_SPACE + "$", "")
                  .trim();
  }
}

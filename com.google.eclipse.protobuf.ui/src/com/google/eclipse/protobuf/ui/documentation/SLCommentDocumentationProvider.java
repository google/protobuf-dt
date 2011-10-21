/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static com.google.eclipse.protobuf.util.CommonWords.space;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * Provides single line comments of a protobuf element as its documentation when hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class SLCommentDocumentationProvider implements IEObjectDocumentationProvider {

  private static final String COMMENT_START = "//\\s*"; // "//" plus any whitespace
  private static final String WINDOWS_NEW_LINE = "\\r\\n";
  private static final String UNIX_NEW_LINE = "\\n";

  @Inject private FieldOptions fieldOptions;
  @Inject private INodes nodes;
  @Inject private Options options;

  @Override public String getDocumentation(EObject o) {
    String comment = findComment(o);
    return comment != null ? comment : "";
  }

  private String findComment(EObject o) {
    EObject target = findRealTarget(o);
    ICompositeNode node = getNode(target);
    if (node == null) return null;
    StringBuilder commentBuilder = new StringBuilder();
    for (INode currentNode : node.getAsTreeIterable()) {
      if (currentNode instanceof ILeafNode && !((ILeafNode) currentNode).isHidden()) break;
      if (currentNode instanceof ILeafNode && nodes.belongsToSingleLineComment(currentNode)) {
        String comment = ((ILeafNode) currentNode).getText();
        commentBuilder.append(cleanUp(comment));
      }
    }
    return commentBuilder.toString().trim();
  }

  private EObject findRealTarget(EObject o) {
    if (o instanceof Option) {
      Property p = options.propertyFrom((Option) o);
      return p != null ? p : o;
    }
    if (o instanceof FieldOption) {
      Property p = fieldOptions.propertyFrom((FieldOption) o);
      return p != null ? p : o;
    }
    return o;
  }

  private String cleanUp(String comment) {
    return comment.replaceFirst(COMMENT_START, "")
                  .replaceAll(WINDOWS_NEW_LINE, space())
                  .replaceAll(UNIX_NEW_LINE, space());
  }
}

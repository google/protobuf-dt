/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static java.util.regex.Pattern.compile;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;

import static com.google.eclipse.protobuf.ui.documentation.Patterns.compileAll;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides multiple-line comments of a protobuf element as its documentation
 * when hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class MLCommentDocumentationProvider implements IEObjectDocumentationProvider {
  private static final Pattern COMMENT = compile("(?s)/\\*\\*?.*");
  private static final Patterns CLEAN_UP = compileAll("\\A/\\*\\*?", "\\*/\\z", "(?m)^( |\\t)*\\** ?",
      "(?m)( |\\t)*\\**( |\\t)*$");

  @Inject private INodes nodes;

  @Override public String getDocumentation(EObject o) {
    String comment = findComment(o);
    return comment != null ? comment : "";
  }

  private String findComment(EObject o) {
    String returnValue = null;
    ICompositeNode node = getNode(o);
    if (node == null) {
      return null;
    }
    // get the last multiple-line comment before a non hidden leaf node
    for (INode currentNode : node.getAsTreeIterable()) {
      if (!nodes.isHiddenLeafNode(currentNode)) {
        continue;
      }
      if (!nodes.isMultipleLineComment(currentNode)) {
        continue;
      }
      String text = ((ILeafNode) currentNode).getText();
      if (COMMENT.matcher(text).matches()) {
        returnValue = cleanUp(text);
      }
    }
    return returnValue;
  }

  private String cleanUp(String comment) {
    String clean = comment;
    for (Pattern pattern : CLEAN_UP) {
      clean = pattern.matcher(clean).replaceAll("");
    }
    return clean.trim();
  }
}

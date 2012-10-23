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
import static com.google.eclipse.protobuf.util.CommonWords.space;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides single-line comments of a protobuf element as its documentation when
 * hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class SLCommentDocumentationProvider implements IEObjectDocumentationProvider {
  private static final Pattern COMMENT_START = compile("//\\s*"); // "//" plus whitespace
  private static final Patterns NEW_LINE = compileAll("\\r\\n", "\\n");

  @Inject private INodes nodes;
  @Inject private Options options;

  @Override public String getDocumentation(EObject o) {
    String comment = findComment(o);
    return comment != null ? comment : "";
  }

  private String findComment(EObject o) {
    EObject target = findRealTarget(o);
    ICompositeNode node = getNode(target);
    if (node == null) {
      return null;
    }
    StringBuilder commentBuilder = new StringBuilder();
    for (INode currentNode : node.getAsTreeIterable()) {
      if (!nodes.isHiddenLeafNode(currentNode)) {
        continue;
      }
      if (!nodes.isSingleLineComment(currentNode)) {
        continue;
      }
      String comment = ((ILeafNode) currentNode).getText();
      commentBuilder.append(cleanUp(comment));
    }
    return commentBuilder.toString().trim();
  }

  private EObject findRealTarget(EObject o) {
    if (o instanceof AbstractOption) {
      IndexedElement e = options.rootSourceOf((AbstractOption) o);
      return e != null ? e : o;
    }
    return o;
  }

  private String cleanUp(String comment) {
    String clean = COMMENT_START.matcher(comment).replaceFirst("");
    for (Pattern pattern : NEW_LINE) {
      clean = pattern.matcher(clean).replaceAll(space());
    }
    return clean;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;
import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import java.util.*;
import java.util.regex.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.util.*;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class CommentNodesFinder {

  private static final String MATCH_ANYTHING = ".*";

  @Inject private INodes nodes;
  @Inject private final IResourceScopeCache cache = IResourceScopeCache.NullImpl.INSTANCE;

  Pair<INode, Matcher> matchingCommentNode(EObject target, String...patternsToMatch) {
    ICompositeNode node = getNode(target);
    for (INode currentNode : node.getAsTreeIterable()) {
      if (currentNode instanceof ILeafNode && !((ILeafNode) currentNode).isHidden()) break;
      if (currentNode instanceof ILeafNode && nodes.belongsToComment(currentNode)) {
        String rawComment = ((ILeafNode) currentNode).getText();
        if (isEmpty(rawComment)) continue;
        String[] comment = rawComment.split(lineSeparator());
        for (String line : comment) {
          for (Pattern pattern : compile(patternsToMatch, target)) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) return pair(currentNode, matcher);
          }
        }
      }
    }
    return null;
  }

  private List<Pattern> compile(String[] patterns, EObject target) {
    List<Pattern> compiled = new ArrayList<Pattern>();
    for (final String s : patterns) {
      Pattern p = cache.get(s, target.eResource(), new Provider<Pattern>() {
        @Override public Pattern get() {
          return Pattern.compile(MATCH_ANYTHING + s + MATCH_ANYTHING, CASE_INSENSITIVE);
        }
      });
      compiled.add(p);
    }
    return compiled;
  }
}

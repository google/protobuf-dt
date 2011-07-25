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
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.scoping.*;
import com.google.eclipse.protobuf.util.CommonWords;
import com.google.inject.Inject;

/**
 * Provides single line comments of a protobuf element as its documentation when hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleLineCommentDocumentationProvider implements IEObjectDocumentationProvider {

  private static final String COMMENT_START = "//\\s*"; // "//" plus any whitespace
  private static final String WINDOWS_NEW_LINE = "\\r\\n";
  private static final String UNIX_NEW_LINE = "\\n";

  @Inject private ProtoDescriptorProvider descriptorProvider;

  private static final String RULE_NAME = "SL_COMMENT";

  public String getDocumentation(EObject o) {
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
      if (currentNode instanceof ILeafNode && isSingleLineCommentTerminalRule(currentNode.getGrammarElement())) {
        String comment = ((ILeafNode) currentNode).getText();
        commentBuilder.append(cleanUp(comment));
      }
    }
    return commentBuilder.toString();
  }

  private EObject findRealTarget(EObject o) {
    if (o instanceof Option) {
      ProtoDescriptor descriptor = descriptorProvider.get();
      Property p = descriptor.lookupOption(((Option) o).getName());
      return p != null ? p : o;
    }
    if (o instanceof FieldOption) {
      ProtoDescriptor descriptor = descriptorProvider.get();
      Property p = descriptor.lookupFieldOption(((FieldOption) o).getName());
      return p != null ? p : o;
    }
    return o;
  }

  private boolean isSingleLineCommentTerminalRule(EObject o) {
    if (!(o instanceof TerminalRule)) return false;
    TerminalRule rule = (TerminalRule) o;
    return RULE_NAME.equalsIgnoreCase(rule.getName());
  }

  private String cleanUp(String comment) {
    return comment.replaceFirst(COMMENT_START, "")
                  .replaceAll(WINDOWS_NEW_LINE, space())
                  .replaceAll(UNIX_NEW_LINE, space());
  }
}

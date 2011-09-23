/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.quickfix;

import static com.google.eclipse.protobuf.ui.quickfix.Messages.*;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.SYNTAX_IS_NOT_PROTO2_ERROR_CODE;

import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.model.edit.*;
import org.eclipse.xtext.ui.editor.quickfix.*;
import org.eclipse.xtext.validation.Issue;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQuickfixProvider extends DefaultQuickfixProvider {

  @Inject private Images images;
  
  @Fix(SYNTAX_IS_NOT_PROTO2_ERROR_CODE) 
  public void makeSyntaxProto2(final Issue issue, IssueResolutionAcceptor acceptor) {
    String image = images.imageFor(Syntax.class);
    acceptor.accept(issue, changeToProto2Label, changeToProto2, image, new ISemanticModification() {
      public void apply(EObject element, IModificationContext context) throws Exception {
        if (!(element instanceof Syntax)) return;
        Syntax syntax = (Syntax) element;
        syntax.setName("proto2");
      }
    });
  }
}

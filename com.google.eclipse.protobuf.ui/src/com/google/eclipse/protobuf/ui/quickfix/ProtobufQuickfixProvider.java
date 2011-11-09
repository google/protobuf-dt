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
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.*;
import static org.eclipse.emf.ecore.util.EcoreUtil.remove;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.model.edit.*;
import org.eclipse.xtext.ui.editor.quickfix.*;
import org.eclipse.xtext.validation.Issue;

import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.ui.labeling.Images;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQuickfixProvider extends DefaultQuickfixProvider {

  @Inject private IndexedElements indexedElements;
  @Inject private Images images;

  @Fix(SYNTAX_IS_NOT_PROTO2_ERROR)
  public void makeSyntaxProto2(Issue issue, IssueResolutionAcceptor acceptor) {
    String image = images.imageFor(Syntax.class);
    acceptor.accept(issue, changeToProto2Label, changeToProto2, image, new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        if (!(element instanceof Syntax)) return;
        Syntax syntax = (Syntax) element;
        syntax.setName("proto2");
      }
    });
  }

  @Fix(INVALID_FIELD_TAG_NUMBER_ERROR)
  public void regenerateTagNumber(Issue issue, IssueResolutionAcceptor acceptor) {
    acceptor.accept(issue, regenerateTagNumberLabel, regenerateTagNumber, "property.gif", new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        if (!(element instanceof IndexedElement)) return;
        IndexedElement e = (IndexedElement) element;
        long tagNumber = indexedElements.calculateTagNumberOf(e);
        indexedElements.setIndexTo(e, tagNumber);
      }
    });
  }

  @Fix(MORE_THAN_ONE_PACKAGE_ERROR)
  public void removeDuplicatePackage(Issue issue, IssueResolutionAcceptor acceptor) {
    acceptor.accept(issue, removeDuplicatePackageLabel, removeDuplicatePackage, "remove.gif",
        new ISemanticModification() {
          @Override public void apply(EObject element, IModificationContext context) throws Exception {
            if (!(element instanceof Package)) return;
            Package aPackage = (Package) element;
            remove(aPackage);
          }
        });
  }
}

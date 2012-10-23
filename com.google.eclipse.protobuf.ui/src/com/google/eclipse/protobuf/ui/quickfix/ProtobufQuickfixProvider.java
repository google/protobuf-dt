/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.quickfix;

import static org.eclipse.emf.ecore.util.EcoreUtil.remove;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findActualNodeFor;

import static com.google.eclipse.protobuf.grammar.Syntaxes.proto2;
import static com.google.eclipse.protobuf.protobuf.BOOL.FALSE;
import static com.google.eclipse.protobuf.protobuf.BOOL.TRUE;
import static com.google.eclipse.protobuf.ui.quickfix.Messages.changeValueDescription;
import static com.google.eclipse.protobuf.ui.quickfix.Messages.changeValueLabel;
import static com.google.eclipse.protobuf.ui.quickfix.Messages.regenerateTagNumberDescription;
import static com.google.eclipse.protobuf.ui.quickfix.Messages.regenerateTagNumberLabel;
import static com.google.eclipse.protobuf.ui.quickfix.Messages.removeDuplicatePackageLabel;
import static com.google.eclipse.protobuf.util.Strings.quote;
import static com.google.eclipse.protobuf.validation.DataTypeValidator.EXPECTED_BOOL_ERROR;
import static com.google.eclipse.protobuf.validation.DataTypeValidator.EXPECTED_STRING_ERROR;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.INVALID_FIELD_TAG_NUMBER_ERROR;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MORE_THAN_ONE_PACKAGE_ERROR;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.SYNTAX_IS_NOT_PROTO2_ERROR;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.BOOL;
import com.google.eclipse.protobuf.protobuf.BooleanLink;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.ProtobufFactory;
import com.google.eclipse.protobuf.protobuf.StringLink;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.protobuf.Value;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufQuickfixProvider extends DefaultQuickfixProvider {
  private static final String ICON_FOR_CHANGE = "change.gif";

  @Inject private IndexedElements indexedElements;
  @Inject private NameResolver nameResolver;
  @Inject private INodes nodes;

  @Fix(SYNTAX_IS_NOT_PROTO2_ERROR)
  public void changeSyntaxToProto2(Issue issue, IssueResolutionAcceptor acceptor) {
    ISemanticModification modification = new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        Syntax syntax = (Syntax) element;
        syntax.setName(proto2());
      }
    };
    String description = String.format(changeValueDescription, "syntax", quote(proto2()));
    String label = String.format(changeValueLabel, proto2());
    acceptor.accept(issue, label, description, ICON_FOR_CHANGE, modification);
  }

  @Fix(INVALID_FIELD_TAG_NUMBER_ERROR)
  public void regenerateTagNumber(Issue issue, IssueResolutionAcceptor acceptor) {
    ISemanticModification modification = new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        IndexedElement e = (IndexedElement) element;
        long tagNumber = indexedElements.calculateNewIndexFor(e);
        indexedElements.setIndexTo(e, tagNumber);
      }
    };
    acceptor.accept(issue, regenerateTagNumberLabel, regenerateTagNumberDescription, "field.gif", modification);
  }

  @Fix(MORE_THAN_ONE_PACKAGE_ERROR)
  public void removeDuplicatePackage(Issue issue, IssueResolutionAcceptor acceptor) {
    final Package aPackage = element(issue, Package.class);
    if (aPackage == null) {
      return;
    }
    ISemanticModification modification = new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        if (element == aPackage) {
          remove(aPackage);
        }
      }
    };
    INode node = findActualNodeFor(aPackage);
    String description = nodes.textOf(node);
    acceptor.accept(issue, removeDuplicatePackageLabel, description, "remove.gif", modification);
  }

  @Fix(EXPECTED_BOOL_ERROR)
  public void changeValueToTrue(Issue issue, IssueResolutionAcceptor acceptor) {
    EObject element = elementIn(issue);
    if (element instanceof FieldOption) {
      FieldOption option = (FieldOption) element;
      changeValue(option, linkTo(TRUE), CommonKeyword.TRUE, issue, acceptor);
    }
  }

  @Fix(EXPECTED_BOOL_ERROR)
  public void changeValueToFalse(Issue issue, IssueResolutionAcceptor acceptor) {
    EObject element = elementIn(issue);
    if (element instanceof FieldOption) {
      FieldOption option = (FieldOption) element;
      changeValue(option, linkTo(FALSE), CommonKeyword.FALSE, issue, acceptor);
    }
  }

  // TODO rename BooleanLink to BoolLink
  private BooleanLink linkTo(BOOL value) {
    BooleanLink link = ProtobufFactory.eINSTANCE.createBooleanLink();
    link.setTarget(value);
    return link;
  }

  @Fix(EXPECTED_STRING_ERROR)
  public void changeValueToEmptyString(Issue issue, IssueResolutionAcceptor acceptor) {
    EObject element = elementIn(issue);
    if (element instanceof FieldOption) {
      FieldOption option = (FieldOption) element;
      String valueToPropose = "";
      changeValue(option, linkTo(valueToPropose), valueToPropose, issue, acceptor);
    }
  }

  private StringLink linkTo(String value) {
    StringLink link = ProtobufFactory.eINSTANCE.createStringLink();
    link.setTarget(value);
    return link;
  }

  private void changeValue(final FieldOption option, final Value newValue, Object proposedValue, Issue issue,
      IssueResolutionAcceptor acceptor) {
    ISemanticModification modification = new ISemanticModification() {
      @Override public void apply(EObject element, IModificationContext context) throws Exception {
        option.setValue(newValue);
      }
    };
    String name = nameResolver.nameOf(option);
    String description = String.format(changeValueDescription, name, proposedValue);
    String label = String.format(changeValueLabel, proposedValue);
    acceptor.accept(issue, label, description, ICON_FOR_CHANGE, modification);
  }

  private EObject elementIn(Issue issue) {
    return element(issue, EObject.class);
  }

  private <T extends EObject> T element(final Issue issue, final Class<T> type) {
    IModificationContext modificationContext = getModificationContextFactory().createModificationContext(issue);
    IXtextDocument xtextDocument = modificationContext.getXtextDocument();
    return xtextDocument.readOnly(new IUnitOfWork<T, XtextResource>() {
      @Override public T exec(XtextResource state) throws Exception {
        EObject e = state.getEObject(issue.getUriToProblem().fragment());
        return (type.isInstance(e)) ? type.cast(e) : null;
      }
    });
  }
}

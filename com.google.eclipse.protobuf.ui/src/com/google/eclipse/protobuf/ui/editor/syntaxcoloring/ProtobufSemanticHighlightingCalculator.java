/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

  @Inject private ModelNodes nodes;

  public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
    if (resource == null) return;
    EList<EObject> contents = resource.getContents();
    if (contents == null || contents.isEmpty()) return;
    Protobuf protobuf = (Protobuf) contents.get(0);
    highlightAllFeatures(protobuf, acceptor, DEFAULT_ID);
  }

  private void highlightAllFeatures(Protobuf protobuf, IHighlightedPositionAcceptor acceptor, String highlightId) {
    highlightElementFeatures(protobuf, acceptor, highlightId);
  }

  private void highlightElementFeatures(Protobuf protobuf, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (ProtobufElement element : protobuf.getElements()) {
      if (element instanceof Package || element instanceof Option) {
        highlightName(element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Type) {
        highlightName(element, acceptor, highlightId);
        if (element instanceof Message) highlightElementFeatures((Message) element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Service) {
        highlightName(element, acceptor, highlightId);
        highlightRpcFeatures((Service) element, acceptor, highlightId);
      }
    }
  }

  private void highlightElementFeatures(Message message, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (MessageElement element : message.getElements()) {
      if (element instanceof Option) {
        highlightName(element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Field) {
        highlightName(element, acceptor, highlightId);
        highlightFieldOptionFeatures((Field) element, acceptor, highlightId);
      }
      if (element instanceof Property) {
        highlightElementFeatures((Property) element, acceptor);
      }
      if (element instanceof Group) {
        highlightElementFeatures((Group) element, acceptor, highlightId);
      }
    }
  }

  private void highlightElementFeatures(Property property, IHighlightedPositionAcceptor acceptor) {
    ValueRef defaultValue = property.getDefault();
    if (!(defaultValue instanceof NumberRef)) return;
    highlightFirstFeature(property, PROPERTY__DEFAULT, acceptor, NUMBER_ID);
  }

  private void highlightElementFeatures(Group group, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (GroupElement e : group.getElements()) {
      if (e instanceof Property) {
        Field field = (Field) e;
        highlightName(field, acceptor, highlightId);
        highlightFieldOptionFeatures(field, acceptor, highlightId);
      }
    }
  }

  private void highlightFieldOptionFeatures(Field field, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (FieldOption option : field.getFieldOptions()) {
      highlightName(option, acceptor, highlightId);
    }
  }

  private void highlightRpcFeatures(Service service, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (Rpc rpc : service.getRpcs()) {
      highlightName(rpc, acceptor, highlightId);
      for (Option option : rpc.getOptions()) {
        highlightName(option, acceptor, highlightId);
      }
    }
  }

  private void highlightName(EObject o, IHighlightedPositionAcceptor acceptor, String highlightId) {
    highlightFirstFeature(o, o.eClass().getEStructuralFeature("name"), acceptor, highlightId);
  }

  private void highlightFirstFeature(EObject semantic, EStructuralFeature feature,
      IHighlightedPositionAcceptor acceptor, String highlightId) {
    INode node = nodes.firstNodeForFeature(semantic, feature);
    if (node == null || node.getText() == null) return;
    acceptor.addPosition(node.getOffset(), node.getText().trim().length(), highlightId);
  }
}

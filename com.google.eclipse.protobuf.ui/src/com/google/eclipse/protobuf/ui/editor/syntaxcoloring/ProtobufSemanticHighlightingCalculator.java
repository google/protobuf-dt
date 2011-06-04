/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findNodesForFeature;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.DEFAULT_ID;

import java.util.List;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

  public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
    if (resource.getContents().isEmpty()) return;
    Protobuf protobuf = (Protobuf) resource.getContents().get(0);
    highlightAllNames(protobuf, acceptor, DEFAULT_ID);
  }

  private void highlightAllNames(Protobuf protobuf, IHighlightedPositionAcceptor acceptor, String highlightId) {
    highlightElementNames(protobuf, acceptor, highlightId);
  }

  private void highlightElementNames(Protobuf protobuf, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (ProtobufElement element : protobuf.getElements()) {
      if (element instanceof Package || element instanceof Option) {
        highlightName(element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Type) {
        highlightName(element, acceptor, highlightId);
        if (element instanceof Message) highlightElementNames((Message) element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Service) {
        highlightName(element, acceptor, highlightId);
        highlightRpcNames((Service) element, acceptor, highlightId);
      }
    }
  }

  private void highlightElementNames(Message message, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (MessageElement element : message.getElements()) {
      if (element instanceof Option) {
        highlightName(element, acceptor, highlightId);
        continue;
      }
      if (element instanceof Field) {
        highlightName(element, acceptor, highlightId);
        highLightFieldOptionNames((Field) element, acceptor, highlightId);
      }
      if (element instanceof Group) {
        for (Property property : ((Group) element).getElements()) {
          highlightName(property, acceptor, highlightId);
          highLightFieldOptionNames(property, acceptor, highlightId);
        }
      }
    }
  }

  private void highLightFieldOptionNames(Field field, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (FieldOption option : field.getFieldOptions()) {
      highlightName(option, acceptor, highlightId);
    }
  }

  private void highlightRpcNames(Service service, IHighlightedPositionAcceptor acceptor, String highlightId) {
    for (Rpc rpc : service.getRpcs()) {
      highlightName(rpc, acceptor, highlightId);
      for (Option option : rpc.getOptions()) {
        highlightName(option, acceptor, highlightId);
      }
    }
  }

  private void highlightName(EObject o, IHighlightedPositionAcceptor acceptor, String highlightId) {
    highlightFirstFeature(o, o.eClass().getEStructuralFeature("name"), highlightId, acceptor);
  }

  private void highlightFirstFeature(EObject semantic, EStructuralFeature feature, String highlightId,
      IHighlightedPositionAcceptor acceptor) {
    INode node = firstFeatureNode(semantic, feature);
    if (node == null || node.getText() == null) return;
    acceptor.addPosition(node.getOffset(), node.getText().length(), highlightId);
  }

  public INode firstFeatureNode(EObject semantic, EStructuralFeature feature) {
    List<INode> nodes = findNodesForFeature(semantic, feature);
    return (nodes.size() == 1) ? nodes.get(0) : null;
  }
}

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

import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.DEFAULT_ID;
import static org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration.NUMBER_ID;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.ABSTRACT_OPTION__VALUE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.LITERAL__INDEX;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__TYPE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION__SOURCE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.RPC__ARG_TYPE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.RPC__RETURN_TYPE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.TYPE_EXTENSION__TYPE;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.ENUM_DEFINITION_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.ENUM_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.ENUM_LITERAL_DEFINITION;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.ENUM_LITERAL_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.ENUM_LITERAL_INDEX_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.MESSAGE_DEFINITION_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.MESSAGE_FIELD_INDEX_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.MESSAGE_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.RPC_ARGUMENT_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.RPC_DEFINITION_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.RPC_RETURN_TYPE_ID;
import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.HighlightingConfiguration.SERVICE_DEFINITION_ID;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.EnumElement;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.GroupElement;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NumberLink;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Service;
import com.google.eclipse.protobuf.protobuf.ServiceElement;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.eclipse.protobuf.protobuf.TypeLink;
import com.google.eclipse.protobuf.protobuf.Value;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {
  @Inject private IndexedElements indexedElements;
  @Inject private INodes nodes;
  @Inject private Options options;

  @Override public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
    if (resource == null) {
      return;
    }
    EList<EObject> contents = resource.getContents();
    if (contents == null || contents.isEmpty()) {
      return;
    }
    Protobuf protobuf = (Protobuf) contents.get(0);
    highlight(protobuf, acceptor);
  }

  private void highlight(Protobuf protobuf, IHighlightedPositionAcceptor acceptor) {
    for (ProtobufElement element : protobuf.getElements()) {
      if (element instanceof Package) {
        highlightName(element, acceptor, DEFAULT_ID);
        continue;
      }
      if (element instanceof Option) {
        highlight((Option) element, acceptor);
        continue;
      }
      if (element instanceof ComplexType) {
        highlight((ComplexType) element, acceptor);
        continue;
      }
      if (element instanceof TypeExtension) {
        highlight((TypeExtension) element, acceptor);
        continue;
      }
      if (element instanceof Service) {
        highlight((Service) element, acceptor);
      }
    }
  }

  private void highlight(TypeExtension extension, IHighlightedPositionAcceptor acceptor) {
    highlightFirstFeature(extension, TYPE_EXTENSION__TYPE, acceptor, MESSAGE_ID);
    for (MessageElement element : extension.getElements()) {
      highlight(element, acceptor);
    }
  }

  private void highlight(ComplexType type, IHighlightedPositionAcceptor acceptor) {
    if (type instanceof Message) {
      highlight((Message) type, acceptor);
      return;
    }
    if (type instanceof Enum) {
      highlight((Enum) type, acceptor);
    }
  }

  private void highlight(Message message, IHighlightedPositionAcceptor acceptor) {
    highlightName(message, acceptor, MESSAGE_DEFINITION_ID);
    for (MessageElement element : message.getElements()) {
      highlight(element, acceptor);
    }
  }

  private void highlight(MessageElement element, IHighlightedPositionAcceptor acceptor) {
    if (element instanceof Option) {
      highlight((Option) element, acceptor);
      return;
    }
    if (element instanceof IndexedElement) {
      highlight((IndexedElement) element, acceptor);
      return;
    }
    if (element instanceof ComplexType) {
      highlight((ComplexType) element, acceptor);
      return;
    }
    if (element instanceof TypeExtension) {
      highlight((TypeExtension) element, acceptor);
    }
  }

  private void highlight(IndexedElement element, IHighlightedPositionAcceptor acceptor) {
    highlightName(element, acceptor, DEFAULT_ID);
    highlightFirstFeature(element, indexedElements.indexFeatureOf(element), acceptor, MESSAGE_FIELD_INDEX_ID);
    highlightOptions(element, acceptor);
    if (element instanceof Group) {
      highlight((Group) element, acceptor);
      return;
    }
    if (element instanceof MessageField) {
      highlight((MessageField) element, acceptor);
    }
  }

  private void highlightOptions(IndexedElement element, IHighlightedPositionAcceptor acceptor) {
    for (FieldOption option : indexedElements.fieldOptionsOf(element)) {
      Value value = option.getValue();
      if (value instanceof LiteralLink) {
        highlightFirstFeature(option, ABSTRACT_OPTION__VALUE, acceptor, ENUM_LITERAL_ID);
        return;
      }
      if (value instanceof NumberLink) {
        highlightFirstFeature(option, ABSTRACT_OPTION__VALUE, acceptor, NUMBER_ID);
      }
    }
  }

  private void highlight(Group group, IHighlightedPositionAcceptor acceptor) {
    for (GroupElement e : group.getElements()) {
      if (e instanceof IndexedElement) {
        highlight((IndexedElement) e, acceptor);
        continue;
      }
      if (e instanceof Enum) {
        highlight((Enum) e, acceptor);
      }
    }
  }

  private void highlight(MessageField field, IHighlightedPositionAcceptor acceptor) {
    highlightPropertyType(field, acceptor);
  }

  private void highlightPropertyType(MessageField field, IHighlightedPositionAcceptor acceptor) {
    TypeLink link = field.getType();
    if (!(link instanceof ComplexTypeLink)) {
      return;
    }
    ComplexType type = ((ComplexTypeLink) link).getTarget();
    if (type instanceof Message) {
      highlightFirstFeature(field, MESSAGE_FIELD__TYPE, acceptor, MESSAGE_ID);
      return;
    }
    if (type instanceof Enum) {
      highlightFirstFeature(field, MESSAGE_FIELD__TYPE, acceptor, ENUM_ID);
      return;
    }
  }

  private void highlight(Enum anEnum, IHighlightedPositionAcceptor acceptor) {
    highlightName(anEnum, acceptor, ENUM_DEFINITION_ID);
    for (EnumElement element : anEnum.getElements()) {
      if (element instanceof Literal) {
        highLight((Literal) element, acceptor);
        continue;
      }
      if (element instanceof Option) {
        highlight((Option) element, acceptor);
      }
    }
  }

  private void highLight(Literal literal, IHighlightedPositionAcceptor acceptor) {
    highlightName(literal, acceptor, ENUM_LITERAL_DEFINITION);
    highlightFirstFeature(literal, LITERAL__INDEX, acceptor, ENUM_LITERAL_INDEX_ID);
  }

  private void highlight(Service service, IHighlightedPositionAcceptor acceptor) {
    highlightName(service, acceptor, SERVICE_DEFINITION_ID);
    for (ServiceElement e : service.getElements()) {
      if (e instanceof Rpc) {
        highlight((Rpc) e, acceptor);
        continue;
      }
      if (e instanceof Option) {
        highlight((Option) e, acceptor);
      }
    }
  }

  private void highlight(Rpc rpc, IHighlightedPositionAcceptor acceptor) {
    highlightName(rpc, acceptor, RPC_DEFINITION_ID);
    highlightFirstFeature(rpc, RPC__ARG_TYPE, acceptor, RPC_ARGUMENT_ID);
    highlightFirstFeature(rpc, RPC__RETURN_TYPE, acceptor, RPC_RETURN_TYPE_ID);
    for (Option option : rpc.getOptions()) {
      highlight(option, acceptor);
    }
  }

  private void highlight(Option option, IHighlightedPositionAcceptor acceptor) {
    IndexedElement element = options.rootSourceOf(option);
    if (element != null) {
      highlightFirstFeature(option, OPTION__SOURCE, acceptor, DEFAULT_ID);
    }
    Value value = option.getValue();
    if (value instanceof LiteralLink) {
      highlightFirstFeature(option, ABSTRACT_OPTION__VALUE, acceptor, ENUM_LITERAL_ID);
      return;
    }
    if (value instanceof NumberLink) {
      highlightFirstFeature(option, ABSTRACT_OPTION__VALUE, acceptor, NUMBER_ID);
    }
  }

  private void highlightName(EObject o, IHighlightedPositionAcceptor acceptor, String highlightId) {
    highlightFirstFeature(o, o.eClass().getEStructuralFeature("name"), acceptor, highlightId);
  }

  private void highlightFirstFeature(EObject semantic, EStructuralFeature feature,
      IHighlightedPositionAcceptor acceptor, String highlightId) {
    INode node = nodes.firstNodeForFeature(semantic, feature);
    if (node == null) {
      return;
    }
    acceptor.addPosition(node.getOffset(), node.getLength(), highlightId);
  }
}

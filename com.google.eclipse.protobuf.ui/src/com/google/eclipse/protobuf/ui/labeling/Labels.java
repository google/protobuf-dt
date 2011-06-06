/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findNodesForFeature;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.nodemodel.INode;

import java.util.List;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.util.Properties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Labels {

  @Inject private Properties properties;

  public Object labelFor(Object o) {
    if (o instanceof ExtendMessage) {
      ExtendMessage extend = (ExtendMessage) o;
      return labelFor(extend);
    }
    if (o instanceof Import) {
      Import i = (Import) o;
      return labelFor(i);
    }
    if (o instanceof Literal) {
      Literal l = (Literal) o;
      return labelFor(l);
    }
    if (o instanceof Property) {
      Property p = (Property) o;
      return labelFor(p);
    }
    if (o instanceof Rpc) {
      Rpc r = (Rpc) o;
      return labelFor(r);
    }
    return null;
  }

  private Object labelFor(ExtendMessage extend) {
    return messageName(extend.getMessage());
  }

  private Object labelFor(Import i) {
    List<INode> nodes = findNodesForFeature(i, IMPORT__IMPORT_URI);
    if (nodes.size() != 1) return i.getImportURI();
    INode node = nodes.get(0);
    return node.getText();
  }

  private Object labelFor(Literal l) {
    StyledString text = new StyledString(l.getName());
    String index = String.format(" [%d]", l.getIndex());
    text.append(index, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(Property p) {
    StyledString text = new StyledString(p.getName());
    String typeName = properties.typeNameOf(p);
    if (typeName == null) typeName = "<unable to resolve type reference>";
    String indexAndType = String.format(" [%d] : %s", p.getIndex(), typeName);
    text.append(indexAndType, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(Rpc r) {
    StyledString text = new StyledString(r.getName());
    String types = String.format(" : %s > %s", messageName(r.getArgType()), messageName(r.getReturnType()));
    text.append(types, DECORATIONS_STYLER);
    return text;
  }

  private String messageName(MessageReference r) {
    return r.getType().getName();
  }
}

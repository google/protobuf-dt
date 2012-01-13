/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Labels {
  @Inject private NameResolver nameResolver;
  @Inject private MessageFields messageFields;
  @Inject private INodes nodes;
  @Inject private Options options;

  public Object labelFor(Object o) {
    if (o instanceof Extensions) {
      Extensions extensions = (Extensions) o;
      return labelFor(extensions);
    }
    if (o instanceof Import) {
      Import anImport = (Import) o;
      return labelFor(anImport);
    }
    if (o instanceof Literal) {
      Literal literal = (Literal) o;
      return labelFor(literal);
    }
    if (o instanceof TypeExtension) {
      TypeExtension extend = (TypeExtension) o;
      return labelFor(extend);
    }
    if (o instanceof MessageField) {
      MessageField field = (MessageField) o;
      return labelFor(field);
    }
    if (o instanceof AbstractOption) {
      AbstractOption option = (AbstractOption) o;
      return labelFor(option);
    }
    if (o instanceof Rpc) {
      Rpc rpc = (Rpc) o;
      return labelFor(rpc);
    }
    if (o instanceof EObject) {
      return nameResolver.nameOf((EObject) o);
    }
    return null;
  }

  private Object labelFor(Extensions extensions) {
    StringBuilder builder = new StringBuilder();
    EList<Range> ranges = extensions.getRanges();
    int rangeCount = ranges.size();
    for (int i = 0; i < rangeCount; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      Range range = ranges.get(i);
      builder.append(range.getFrom());
      String to = range.getTo();
      if (to != null) {
        builder.append(" > ").append(to);
      }
    }
    return builder.toString();
  }

  private Object labelFor(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) {
      return anImport.getImportURI();
    }
    return node.getText();
  }

  private Object labelFor(Literal literal) {
    StyledString text = new StyledString(nameResolver.nameOf(literal));
    String index = String.format(" [%d]", literal.getIndex());
    text.append(index, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(TypeExtension extension) {
    return typeName(extension.getType());
  }

  private String typeName(ExtensibleTypeLink link) {
    ExtensibleType type = link.getTarget();
    if (type == null) {
      return null;
    }
    return nameResolver.nameOf(type);
  }

  private Object labelFor(MessageField field) {
    StyledString text = new StyledString(nameResolver.nameOf(field));
    String typeName = messageFields.typeNameOf(field);
    if (typeName == null) {
      typeName = "<unresolved reference>"; // TODO move to properties file
    }
    String indexAndType = String.format(" [%d] : %s", field.getIndex(), typeName);
    text.append(indexAndType, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(AbstractOption option) {
    IndexedElement e = options.rootSourceOf(option);
    String name = options.nameForOption(e);
    if (option instanceof AbstractCustomOption) {
      AbstractCustomOption customOption = (AbstractCustomOption) option;
      StringBuilder b = new StringBuilder();
      b.append(formatCustomOptionName(name));
      for (OptionField field : options.fieldsOf(customOption)) {
        IndexedElement source = field.getTarget();
        b.append(".")
         .append(options.nameForOption(source));
      }
      return b.toString();
    }
    return name;
  }

  private String formatCustomOptionName(String name) {
    return String.format("(%s)", name);
  }

  private Object labelFor(Rpc rpc) {
    StyledString text = new StyledString(nameResolver.nameOf(rpc));
    String types = String.format(" : %s > %s", messageName(rpc.getArgType()), messageName(rpc.getReturnType()));
    text.append(types, DECORATIONS_STYLER);
    return text;
  }

  private String messageName(MessageLink link) {
    Message m = link.getTarget();
    if (m == null) {
      return null;
    }
    return nameResolver.nameOf(m);
  }
}

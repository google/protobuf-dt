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

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.nodemodel.INode;

import java.util.List;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Labels {

  @Inject private INodes nodes;
  @Inject private Options options;
  @Inject private Properties properties;

  public Object labelFor(Object o) {
    if (o instanceof Option) {
      Option option = (Option) o;
      return labelFor(option);
    }
    if (o instanceof MessageExtension) {
      MessageExtension extend = (MessageExtension) o;
      return labelFor(extend);
    }
    if (o instanceof Extensions) {
      Extensions extensions = (Extensions) o;
      return labelFor(extensions);
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

  private Object labelFor(Option o) {
    IndexedElement e = options.rootSourceOf(o);
    String name = options.nameForOption(e);
    StringBuilder b = new StringBuilder();
    boolean isCustomOption = o instanceof CustomOption || o instanceof CustomFieldOption;
    if (isCustomOption) b.append("(");
    b.append(name);
    if (isCustomOption) b.append(")");
    if (o instanceof CustomOption) {
      appendFields(b, ((CustomOption) o).getOptionFields());
    }
    if (o instanceof CustomFieldOption) {
      appendFields(b, ((CustomFieldOption) o).getOptionFields());
    }
    return b.toString();
  }
  
  private void appendFields(StringBuilder b, List<OptionFieldSource> fields) {
    for (OptionFieldSource field : fields) {
      b.append(".");
      if (field instanceof OptionMessageFieldSource) {
        IndexedElement source = ((OptionMessageFieldSource) field).getOptionMessageField();
        b.append(options.nameForOption(source));
      }
      if (field instanceof OptionExtendMessageFieldSource) {
        IndexedElement source = ((OptionExtendMessageFieldSource) field).getOptionExtendMessageField();
        b.append("(")
         .append(options.nameForOption(source))
         .append(")");
      }
    }
  }

  private Object labelFor(MessageExtension e) {
    return messageName(e.getMessage());
  }

  private Object labelFor(Extensions e) {
    StringBuilder builder = new StringBuilder();
    EList<Range> ranges = e.getRanges();
    int rangeCount = ranges.size();
    for (int i = 0; i < rangeCount; i++) {
      if (i > 0) builder.append(", ");
      Range range = ranges.get(i);
      builder.append(range.getFrom());
      String to = range.getTo();
      if (to != null) {
        builder.append(" > ").append(to);
      }
    }
    return builder.toString();
  }

  private Object labelFor(Import i) {
    INode node = nodes.firstNodeForFeature(i, IMPORT__IMPORT_URI);
    if (node == null) return i.getImportURI();
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
    if (typeName == null) typeName = "<unresolved reference>"; // TODO move to
                                                               // properties
                                                               // file
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

  private String messageName(MessageRef r) {
    return r.getType().getName();
  }
}

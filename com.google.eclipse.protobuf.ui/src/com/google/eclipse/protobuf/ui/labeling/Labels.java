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

import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;

import static com.google.eclipse.protobuf.ui.labeling.Messages.unresolved;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.StyledString;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.ExtensibleTypeLink;
import com.google.eclipse.protobuf.protobuf.Extensions;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.MessageLink;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.eclipse.protobuf.protobuf.Range;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Labels {
  @Inject private Imports imports;
  @Inject private MessageFields messageFields;
  @Inject private NameResolver nameResolver;
  @Inject private Options options;

  public Object labelFor(Object o) {
    if (o instanceof AbstractOption) {
      AbstractOption option = (AbstractOption) o;
      return labelFor(option);
    }
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
    if (o instanceof MessageField) {
      MessageField field = (MessageField) o;
      return labelFor(field);
    }
    if (o instanceof Rpc) {
      Rpc rpc = (Rpc) o;
      return labelFor(rpc);
    }
    if (o instanceof Stream) {
      Stream stream = (Stream) o;
      return labelFor(stream);
    }
    if (o instanceof TypeExtension) {
      TypeExtension extension = (TypeExtension) o;
      return labelFor(extension);
    }
    if (o instanceof EObject) {
      return nameResolver.nameOf((EObject) o);
    }
    return null;
  }

  private Object labelFor(AbstractOption option) {
    IndexedElement e = options.rootSourceOf(option);
    String name = options.nameForOption(e);
    if (option instanceof AbstractCustomOption) {
      AbstractCustomOption customOption = (AbstractCustomOption) option;
      StringBuilder b = new StringBuilder();
      b.append(formatCustomOptionElement(name));
      for (OptionField field : options.fieldsOf(customOption)) {
        IndexedElement source = field.getTarget();
        String sourceName = options.nameForOption(source);
        b.append(".")
         .append(formatCustomOptionElement(sourceName));
      }
      return b.toString();
    }
    return name;
  }

  private String formatCustomOptionElement(String name) {
    return String.format("(%s)", name);
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
    return imports.uriAsEnteredByUser(anImport);
  }

  private Object labelFor(Literal literal) {
    StyledString text = new StyledString(nameResolver.nameOf(literal));
    String index = String.format(" [%d]", literal.getIndex());
    text.append(index, DECORATIONS_STYLER);
    return text;
  }


  private Object labelFor(MessageField field) {
    StyledString text = new StyledString(nameResolver.nameOf(field));
    String typeName = messageFields.typeNameOf(field);
    if (typeName == null) {
      typeName = unresolved;
    }
    String indexAndType = String.format(" [%d] : %s", field.getIndex(), typeName);
    text.append(indexAndType, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(Rpc rpc) {
    StyledString text = new StyledString(nameResolver.nameOf(rpc));
    String types = String.format(" : %s > %s", nameOf(rpc.getArgType()), nameOf(rpc.getReturnType()));
    text.append(types, DECORATIONS_STYLER);
    return text;
  }

  private Object labelFor(Stream stream) {
    StyledString text = new StyledString(nameResolver.nameOf(stream));
    String types = String.format(" (%s, %s)", nameOf(stream.getClientMessage()), nameOf(stream.getServerMessage()));
    text.append(types, DECORATIONS_STYLER);
    return text;
  }

  private String nameOf(MessageLink link) {
    return nameOf(link.getTarget());
  }

  private Object labelFor(TypeExtension extension) {
    ExtensibleTypeLink type = extension.getType();
    return nameOf(type.getTarget());
  }

  private String nameOf(EObject e) {
    String name = (e == null) ? null : nameResolver.nameOf(e);
    return (name == null) ? unresolved : name;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import static com.google.common.collect.ImmutableList.of;
import static com.google.eclipse.protobuf.ui.outline.Messages.importDeclarations;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;

import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.protobuf.BooleanLink;
import com.google.eclipse.protobuf.protobuf.ExtensibleTypeLink;
import com.google.eclipse.protobuf.protobuf.Extensions;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.MessageLink;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Stream;

/**
 * Customization of the default outline structure.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufOutlineTreeProvider extends DefaultOutlineTreeProvider {
  private static final ImmutableList<Class<? extends EObject>> IGNORED_ELEMENT_TYPES =
      of(BooleanLink.class, FieldOption.class, MessageLink.class, ExtensibleTypeLink.class);

  private static final ImmutableList<Class<? extends EObject>> LEAF_TYPES =
      of(Extensions.class, Import.class, MessageField.class, Option.class, Package.class, Stream.class);

  @Override protected boolean _isLeaf(EObject e) {
    return isInstanceOfAny(e, LEAF_TYPES);
  }

  protected void _createChildren(DocumentRootNode parent, Protobuf protobuf) {
    OutlineViewModel model = new OutlineViewModel(protobuf);
    for (EObject aPackage : model.packages()) {
      createNode(parent, aPackage);
    }
    addGroup(parent, protobuf, model.imports(), "imports", importDeclarations);
    for (EObject e : model.remainingElements()) {
      createNode(parent, e);
    }
  }

  private void addGroup(DocumentRootNode parent, Protobuf protobuf, List<? extends EObject> group, String imageKey,
      String text) {
    if (group.isEmpty()) {
      return;
    }
    SimpleOutlineNode groupNode = new SimpleOutlineNode(parent, protobuf, labelProvider.getImage(imageKey), text, false);
    for (EObject o : group) {
      createNode(groupNode, o);
    }
  }

  @Override protected void createNode(IOutlineNode parent, EObject e) {
    if (isIgnored(e)) {
      return;
    }
    super.createNode(parent, e);
  }

  private boolean isIgnored(EObject e) {
    return isInstanceOfAny(e, IGNORED_ELEMENT_TYPES);
  }

  private boolean isInstanceOfAny(EObject e, List<Class<? extends EObject>> types) {
    for (Class<? extends EObject> type : types) {
      if (type.isInstance(e)) {
        return true;
      }
    }
    return false;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;

import org.eclipse.xtext.nodemodel.INode;

import com.google.eclipse.protobuf.conversion.STRINGValueConverter;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;

/**
 * Utility methods related to <code>{@link Import}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Imports {
  @Inject private INodes nodes;
  @Inject private STRINGValueConverter stringValueConverter;

  public String uriAsEnteredByUser(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) {
      return null;
    }
    String text = node.getText();
    if (text == null) {
      return null;
    }
    return stringValueConverter.toValue(text, node);

  }
}

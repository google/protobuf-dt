/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class SimpleOutlineNode extends AbstractOutlineNode {

  SimpleOutlineNode(IOutlineNode parent, Image image, Object text, boolean isLeaf) {
    super(parent, image, text, isLeaf);
  }
}

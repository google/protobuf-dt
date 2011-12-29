/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class SimpleOutlineNode extends AbstractOutlineNode {
  private final URI ownerUri;

  SimpleOutlineNode(IOutlineNode parent, EObject owner, Image image, Object text, boolean isLeaf) {
    super(parent, image, text, isLeaf);
    ownerUri = EcoreUtil.getURI(owner);
  }

  @Override protected URI getEObjectURI() {
    return ownerUri;
  }
}

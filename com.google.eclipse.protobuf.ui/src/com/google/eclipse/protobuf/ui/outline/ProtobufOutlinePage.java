/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;

/**
 * Outline Page for Protocol Buffer editors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufOutlinePage extends OutlinePage {

  /**
   * Indicates that the root node and its immediate children of the Outline View need to be expanded.
   * @return 3.
   */
  @Override protected int getDefaultExpansionLevel() {
    return 3;
  }
}

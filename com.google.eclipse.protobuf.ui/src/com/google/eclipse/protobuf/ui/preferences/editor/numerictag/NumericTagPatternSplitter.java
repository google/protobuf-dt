/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.numerictag;

import com.google.eclipse.protobuf.ui.preferences.StringSplitter;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NumericTagPatternSplitter extends StringSplitter {
  private static final StringSplitter INSTANCE = new NumericTagPatternSplitter();

  static StringSplitter instance() {
    return INSTANCE;
  }

  private NumericTagPatternSplitter() {
    super("\\t");
  }
}

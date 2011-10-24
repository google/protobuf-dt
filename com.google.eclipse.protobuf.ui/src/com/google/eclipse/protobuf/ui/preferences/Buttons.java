/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * Utility methods related to <code>{@link Button}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Buttons {

  public static Buttons with(Button...buttons) {
    return new Buttons(buttons);
  }

  private final Button[] buttons;

  private Buttons(Button[] buttons) {
    this.buttons = buttons;
  }

  public void add(SelectionListener l) {
    for (Button b : buttons) b.addSelectionListener(l);
  }
}

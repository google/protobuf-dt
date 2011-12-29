/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * Group of <code>{@link Button}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ButtonGroup {
  /**
   * Creates a new <code>{@link ButtonGroup}</code>.
   * @param buttons the buttons to group.
   * @return the created group of buttons.
   */
  public static ButtonGroup with(Button...buttons) {
    return new ButtonGroup(buttons);
  }

  private final Button[] buttons;

  private ButtonGroup(Button[] buttons) {
    this.buttons = buttons;
  }

  /**
   * Adds the given <code>{@link SelectionListener}</code> to all the buttons in this group.
   * @param listener the {@code SelectionListener} to add.
   */
  public void add(SelectionListener listener) {
    for (Button b : buttons) {
      b.addSelectionListener(listener);
    }
  }
}

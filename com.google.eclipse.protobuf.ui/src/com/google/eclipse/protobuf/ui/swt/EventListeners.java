/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swt;

import java.util.Collection;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * Utility methods related to SWT event listeners.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class EventListeners {

  /**
   * Adds the given <code>{@link SelectionListener}</code> to the given <code>{@link Button}</code>s.
   * @param listener the listener to add.
   * @param buttons the buttons to add the listener to.
   */
  public static void addSelectionListener(SelectionListener listener, Collection<Button> buttons) {
    for (Button button : buttons) button.addSelectionListener(listener);
  }

  /**
   * Removes the given <code>{@link SelectionListener}</code> to the given <code>{@link Button}</code>s.
   * @param listener the listener to remove.
   * @param buttons the buttons to remove the listener to.
   */
  public static void removeSelectionListener(SelectionListener listener, Collection<Button> buttons) {
    for (Button button : buttons) button.removeSelectionListener(listener);
  }

  /**
   * Adds the given <code>{@link ModifyListener}</code> to the given <code>{@link Text}</code> widgets.
   * @param listener the listener to add.
   * @param texts the text widgets to add the listener to.
   */
  public static void addModifyListener(ModifyListener listener, Collection<Text> texts) {
    for (Text text : texts) text.addModifyListener(listener);
  }

  private EventListeners() {}
}

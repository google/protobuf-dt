/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import static org.mockito.Mockito.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Button;
import org.junit.*;

import com.google.eclipse.protobuf.ui.preferences.pages.ButtonGroup;

/**
 * Tests for <code>{@link ButtonGroup#add(SelectionListener)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ButtonGroup_add_Test {
  private Button[] buttons;
  private SelectionListener listener;

  @Before public void setUp() {
    buttons = new Button[] { mock(Button.class), mock(Button.class), mock(Button.class) };
    listener = new SelectionAdapter() {};
  }

  @Test public void should_add_SelectionListener_to_all_buttons() {
    ButtonGroup.with(buttons).add(listener);
    for (Button b : buttons) {
      verify(b).addSelectionListener(listener);
    }
  }
}

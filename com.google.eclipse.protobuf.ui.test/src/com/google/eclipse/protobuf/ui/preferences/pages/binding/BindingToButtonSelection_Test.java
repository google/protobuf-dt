/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;

import org.eclipse.swt.widgets.Button;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link BindingToButtonSelection}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToButtonSelection_Test {
  private Button button;
  private BooleanPreference preference;

  private BindingToButtonSelection binding;

  @Before public void setUp() {
    button = mock(Button.class);
    preference = mock(BooleanPreference.class);
    binding = bindSelectionOf(button).to(preference);
  }

  @Test public void should_apply_preference_value_to_selection_in_Button() {
    when(preference.value()).thenReturn(true);
    binding.applyPreferenceValueToTarget();
    verify(preference).value();
    verify(button).setSelection(true);
  }

  @Test public void should_apply_preference_default_value_to_selection_in_Button() {
    when(preference.defaultValue()).thenReturn(true);
    binding.applyDefaultPreferenceValueToTarget();
    verify(preference).defaultValue();
    verify(button).setSelection(true);
  }

  @Test public void should_store_selection_in_Button_into_preference() {
    when(button.getSelection()).thenReturn(true);
    binding.savePreferenceValue();
    verify(button).getSelection();
    verify(preference).updateValue(true);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.binding;

import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToTextValue.bindTextOf;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Text;
import org.junit.*;

import com.google.eclipse.protobuf.ui.preferences.StringPreference;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToTextValue;

/**
 * Tests for <code>{@link BindingToTextValue}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToTextValue_Test {
  private Text text;
  private StringPreference preference;

  private BindingToTextValue binding;

  @Before public void setUp() {
    text = mock(Text.class);
    preference = mock(StringPreference.class);
    binding = bindTextOf(text).to(preference);
  }

  @Test public void should_apply_preference_value_to_selection_in_Button() {
    when(preference.getValue()).thenReturn("Hello World");
    binding.applyPreferenceValueToTarget();
    verify(preference).getValue();
    verify(text).setText("Hello World");
  }

  @Test public void should_apply_preference_default_value_to_selection_in_Button() {
    when(preference.getDefaultValue()).thenReturn("Hello World");
    binding.applyDefaultPreferenceValueToTarget();
    verify(preference).getDefaultValue();
    verify(text).setText("Hello World");
  }

  @Test public void should_store_selection_in_Button_into_preference() {
    when(text.getText()).thenReturn("Hello World");
    binding.savePreferenceValue();
    verify(text).getText();
    verify(preference).setValue("Hello World");
  }
}

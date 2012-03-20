/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import org.eclipse.swt.widgets.Button;


/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToButtonSelection implements Binding {
  private final Button button;
  private final Preference<Boolean> preference;

  public static BindingBuilder bindSelectionOf(Button button) {
    return new BindingBuilder(button);
  }

  private BindingToButtonSelection(Button button, Preference<Boolean> preference) {
    this.preference = preference;
    this.button = button;
  }

  @Override public void applyPreferenceValueToTarget() {
    apply(preference.value());
  }

  @Override public void applyDefaultPreferenceValueToTarget() {
    apply(preference.defaultValue());
  }

  private void apply(boolean value) {
    button.setSelection(value);
  }

  @Override public void savePreferenceValue() {
    preference.updateValue(button.getSelection());
  }

  public static class BindingBuilder {
    private final Button button;

    BindingBuilder(Button button) {
      this.button = button;
    }

    public BindingToButtonSelection to(Preference<Boolean> preference) {
      return new BindingToButtonSelection(button, preference);
    }
  }
}

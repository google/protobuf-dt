/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import org.eclipse.swt.widgets.Text;


/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToTextValue implements Binding {
  private final Text text;
  private final Preference<String> preference;

  public static BindingBuilder bindTextOf(Text text) {
    return new BindingBuilder(text);
  }

  private BindingToTextValue(Text text, Preference<String> preference) {
    this.text = text;
    this.preference = preference;
  }

  @Override public void applyPreferenceValueToTarget() {
    apply(preference.value());
  }

  @Override public void applyDefaultPreferenceValueToTarget() {
    apply(preference.defaultValue());
  }

  private void apply(String value) {
    text.setText(value);
  }

  @Override public void savePreferenceValue() {
    preference.updateValue(text.getText());
  }

  public static class BindingBuilder {
    private final Text text;

    BindingBuilder(Text text) {
      this.text = text;
    }

    public BindingToTextValue to(Preference<String> preference) {
      return new BindingToTextValue(text, preference);
    }
  }
}

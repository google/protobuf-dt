/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.binding;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Text;

import com.google.eclipse.protobuf.ui.preferences.StringPreference;

/**
 * Binds a {@code String} value from a <code>{@link IPreferenceStore}</code> to the value of a
 * <code>{@link Text}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToTextValue implements Binding {

  private final Text text;
  private final StringPreference preference;

  public static BindingBuilder bindTextOf(Text text) {
    return new BindingBuilder(text);
  }

  /**
   * Creates a new </code>{@link BindingToTextValue}</code>.
   * @param text the control to bind to the preference.
   * @param preference the given preference.
   */
  private BindingToTextValue(Text text, StringPreference preference) {
    this.text = text;
    this.preference = preference;
  }

  /** {@inheritDoc} */
  public void applyPreferenceValueToTarget() {
    String value = preference.value();
    text.setText(value);
  }

  /** {@inheritDoc} */
  public void applyDefaultPreferenceValueToTarget() {
    String value = preference.defaultValue();
    text.setText(value);
  }

  /** {@inheritDoc} */
  public void savePreferenceValue() {
    preference.value(text.getText());
  }

  public static class BindingBuilder {
    private final Text text;

    /**
     * Creates a new </code>{@link BindingBuilder}</code>.
     * @param text the text whose value will be bound to a preference value.
     */
    public BindingBuilder(Text text) {
      this.text = text;
    }

    /**
     * Creates a new <code>{@link BindingToTextValue}</code>.
     * @param preference the preference to bind to the value of this builder's text.
     * @return the created binding.
     */
    public BindingToTextValue to(StringPreference preference) {
      return new BindingToTextValue(text, preference);
    }
  }
}

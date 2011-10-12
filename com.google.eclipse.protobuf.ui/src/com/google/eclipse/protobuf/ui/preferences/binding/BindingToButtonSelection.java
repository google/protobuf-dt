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
import org.eclipse.swt.widgets.Button;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * Binds a {@code boolean} value from a <code>{@link IPreferenceStore}</code> to the selection of a
 * <code>{@link Button}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToButtonSelection implements Binding {

  private final Button button;
  private final BooleanPreference preference;

  public static BindingBuilder bindSelectionOf(Button button) {
    return new BindingBuilder(button);
  }

  /**
   * Creates a new </code>{@link BindingToButtonSelection}</code>.
   * @param button the control to bind to the preference.
   * @param preference the given preference.
   */
  private BindingToButtonSelection(Button button, BooleanPreference preference) {
    this.preference = preference;
    this.button = button;
  }

  /** {@inheritDoc} */
  public void applyPreferenceValueToTarget() {
    apply(preference.value());
  }

  /** {@inheritDoc} */
  public void applyDefaultPreferenceValueToTarget() {
    apply(preference.defaultValue());
  }

  private void apply(boolean value) {
    button.setSelection(value);
  }

  /** {@inheritDoc} */
  public void savePreferenceValue() {
    preference.value(button.getSelection());
  }

  public static class BindingBuilder {
    private final Button button;

    /**
     * Creates a new </code>{@link BindingBuilder}</code>.
     * @param button the button whose selection will be bound to a preference value.
     */
    public BindingBuilder(Button button) {
      this.button = button;
    }

    /**
     * Creates a new <code>{@link BindingToButtonSelection}</code>.
     * @param preference the preference to bind to the selection of this builder's button.
     * @return the created binding.
     */
    public BindingToButtonSelection to(BooleanPreference preference) {
      return new BindingToButtonSelection(button, preference);
    }
  }
}

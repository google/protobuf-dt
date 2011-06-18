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

/**
 * Binds a {@code boolean} value from a <code>{@link IPreferenceStore}</code> to the selection of a
 * <code>{@link Button}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceToButtonBinding implements Binding {

  private final String preferenceName;
  private final Button button;

  /**
   * Creates a new </code>{@link PreferenceToButtonBinding}</code>.
   * @param preferenceName the name of the preference to read/write.
   * @param button the control to bind to the preference.
   */
  public PreferenceToButtonBinding(String preferenceName, Button button) {
    this.preferenceName = preferenceName;
    this.button = button;
  }

  /**
   * Reads a {@code boolean} preference from the given <code>{@link IPreferenceStore}</code> and applies it to the
   * selection of this binding's button.
   * @param store the preference store.
   */
  public void read(IPreferenceStore store) {
    boolean value = store.getBoolean(preferenceName);
    button.setSelection(value);
  }

  /**
   * Reads the default {@code boolean} preference value from the given <code>{@link IPreferenceStore}</code> and applies
   * it to the selection of this binding's button.
   * @param store the preference store.
   */
  public void readDefault(IPreferenceStore store) {
    boolean value = store.getDefaultBoolean(preferenceName);
    button.setSelection(value);
  }

  /**
   * Applies the value of the selection of this binding's button to a preference in the given
   * <code>{@link IPreferenceStore}</code>.
   * @param store the preference store.
   */
  public void save(IPreferenceStore store) {
    store.setValue(preferenceName, button.getSelection());
  }
}

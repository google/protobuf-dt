/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.binding;

import java.util.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;

/**
 * Binds preferences to UI controls.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceBinder {

  private final List<Binding> bindings = new ArrayList<Binding>();

  public void bind(String preferenceName, Button button) {
    add(new PreferenceToButtonBinding(preferenceName, button));
  }

  public void add(Binding binding) {
    bindings.add(binding);
  }

  public void readPreferences(IPreferenceStore store) {
    for (Binding binding : bindings) {
      binding.read(store);
    }
  }

  public void readDefaultPreferences(IPreferenceStore store) {
    for (Binding binding : bindings) {
      binding.readDefault(store);
    }
  }

  public void savePreferences(IPreferenceStore store) {
    for (Binding binding : bindings) {
      binding.save(store);
    }
  }
}

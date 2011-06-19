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

/**
 * Binds preferences to UI controls.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceBinder {

  private final List<Binding> allBindings = new ArrayList<Binding>();

  public void addAll(Binding...bindings) {
    for (Binding binding : bindings) add(binding);
  }

  public void add(Binding binding) {
    allBindings.add(binding);
  }

  public void applyValues() {
    for (Binding binding : allBindings) {
      binding.applyPreferenceValueToTarget();
    }
  }

  public void applyDefaults() {
    for (Binding binding : allBindings) {
      binding.applyDefaultPreferenceValueToTarget();
    }
  }

  public void saveValues() {
    for (Binding binding : allBindings) {
      binding.savePreferenceValue();
    }
  }
}

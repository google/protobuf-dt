/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 * Binds preference values to properties in UI controls.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceBinder {
  private final List<Binding> allBindings = newArrayList();

  /**
   * Adds all the given bindings to this binder.
   * @param bindings the bindings to add.
   */
  public void addAll(Binding...bindings) {
    for (Binding binding : bindings) {
      add(binding);
    }
  }

  /**
   * Adds the given binding to this binder.
   * @param binding the binding to add.
   */
  public void add(Binding binding) {
    allBindings.add(binding);
  }

  /**
   * Calls <code>{@link Binding#applyPreferenceValueToTarget()}</code> on each of the bindings in this binder.
   */
  public void applyValues() {
    for (Binding binding : allBindings) {
      binding.applyPreferenceValueToTarget();
    }
  }

  /**
   * Calls <code>{@link Binding#applyDefaultPreferenceValueToTarget()}</code> on each of the bindings in this binder.
   */
  public void applyDefaults() {
    for (Binding binding : allBindings) {
      binding.applyDefaultPreferenceValueToTarget();
    }
  }

  /**
   * Calls <code>{@link Binding#savePreferenceValue()}</code> on each of the bindings in this binder.
   */
  public void saveValues() {
    for (Binding binding : allBindings) {
      binding.savePreferenceValue();
    }
  }
}

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

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link PreferenceBinder}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceBinder_Test {
  private Binding[] bindings;
  private PreferenceBinder binder;

  @Before public void setUp() {
    bindings = new Binding[] { mock(Binding.class), mock(Binding.class), mock(Binding.class), mock(Binding.class) };
    binder = new PreferenceBinder();
    binder.addAll(bindings);
  }

  @Test public void should_apply_preference_values_in_all_bindings() {
    binder.applyValues();
    for (Binding binding : bindings) {
      verify(binding).applyPreferenceValueToTarget();
    }
  }

  @Test public void should_apply_default_preference_values_in_all_bindings() {
    binder.applyDefaults();
    for (Binding binding : bindings) {
      verify(binding).applyDefaultPreferenceValueToTarget();
    }
  }

  @Test public void should_save_preference_values_in_all_bindings() {
    binder.saveValues();
    for (Binding binding : bindings) {
      verify(binding).savePreferenceValue();
    }
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link BooleanPreference}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BooleanPreference_Test {
  private String name;
  private IPreferenceStore store;

  private BooleanPreference preference;

  @Before public void setUp() {
    name = "active";
    store = mock(IPreferenceStore.class);
    preference = new BooleanPreference(name, store);
  }

  @Test public void should_read_value_from_IPreferenceStore() {
    when(store.getBoolean(name)).thenReturn(true);
    assertTrue(preference.value());
    verify(store).getBoolean(name);
  }

  @Test public void should_read_default_value_from_IPreferenceStore() {
    when(store.getDefaultBoolean(name)).thenReturn(true);
    assertTrue(preference.defaultValue());
    verify(store).getDefaultBoolean(name);
  }

  @Test public void should_update_value_in_IPreferenceStore() {
    preference.updateValue(true);
    verify(store).setValue(name, true);
  }
}

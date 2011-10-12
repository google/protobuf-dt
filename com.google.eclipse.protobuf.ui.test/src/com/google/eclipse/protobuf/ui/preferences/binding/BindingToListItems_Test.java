/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.binding;

import static com.google.eclipse.protobuf.ui.preferences.binding.BindingToListItems.bindItemsOf;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.List;
import org.junit.*;

import com.google.eclipse.protobuf.ui.preferences.StringListPreference;

/**
 * Tests for <code>{@link BindingToListItems}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToListItems_Test {

  private List list;
  private StringListPreference preference;

  private BindingToListItems binding;

  @Before public void setUp() {
    list = mock(List.class);
    preference = mock(StringListPreference.class);
    binding = bindItemsOf(list).to(preference);
  }

  @Test public void should_apply_preference_value_to_selection_in_Button() {
    when(preference.value()).thenReturn(asList("One" , "Two"));
    binding.applyPreferenceValueToTarget();
    verify(preference).value();
    verify(list).removeAll();
    verify(list).add("One");
    verify(list).add("Two");
  }

  @Test public void should_apply_preference_default_value_to_selection_in_Button() {
    when(preference.defaultValue()).thenReturn(asList("One" , "Two"));
    binding.applyDefaultPreferenceValueToTarget();
    verify(preference).defaultValue();
    verify(list).removeAll();
    verify(list).add("One");
    verify(list).add("Two");
  }

  @Test public void should_store_selection_in_Button_into_preference() {
    when(list.getItems()).thenReturn(new String[] { "One", "Two" });
    binding.savePreferenceValue();
    verify(list).getItems();
    verify(preference).value(asList("One" , "Two"));
  }
}

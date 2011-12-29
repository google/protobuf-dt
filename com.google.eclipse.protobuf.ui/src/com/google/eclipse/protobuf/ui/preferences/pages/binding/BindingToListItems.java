/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.List;

import com.google.eclipse.protobuf.ui.preferences.StringListPreference;

/**
 * Binds a {@code String} value from a <code>{@link IPreferenceStore}</code> to a list of items of a
 * <code>{@link List}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToListItems implements Binding {
  private final List list;
  private final StringListPreference preference;

  public static BindingBuilder bindItemsOf(List list) {
    return new BindingBuilder(list);
  }

  /**
   * Creates a new </code>{@link BindingToListItems}</code>.
   * @param list the control to bind to the preference.
   * @param preference the given preference.
   */
  private BindingToListItems(List list, StringListPreference preference) {
    this.list = list;
    this.preference = preference;
  }

  /** {@inheritDoc} */
  @Override public void applyPreferenceValueToTarget() {
    apply(preference.getValue());
  }

  /** {@inheritDoc} */
  @Override public void applyDefaultPreferenceValueToTarget() {
    apply(preference.getDefaultValue());
  }

  private void apply(Collection<String> value) {
    list.removeAll();
    for (String s : value) {
      list.add(s);
    }
  }

  /** {@inheritDoc} */
  @Override public void savePreferenceValue() {
    preference.setValue(asList(list.getItems()));
  }

  public static class BindingBuilder {
    private final List list;

    /**
     * Creates a new </code>{@link BindingBuilder}</code>.
     * @param list the list whose items will be bound to a preference value.
     */
    public BindingBuilder(List list) {
      this.list = list;
    }

    /**
     * Creates a new <code>{@link BindingToListItems}</code>.
     * @param preference the preference to bind to the value of this builder's list.
     * @return the created binding.
     */
    public BindingToListItems to(StringListPreference preference) {
      return new BindingToListItems(list, preference);
    }
  }
}

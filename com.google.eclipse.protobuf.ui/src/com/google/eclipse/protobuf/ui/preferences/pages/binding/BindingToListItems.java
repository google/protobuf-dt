/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.Collection;

import org.eclipse.swt.widgets.List;

import com.google.common.collect.ImmutableList;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BindingToListItems implements Binding {
  private final List list;
  private final Preference<ImmutableList<String>> preference;

  public static BindingBuilder bindItemsOf(List list) {
    return new BindingBuilder(list);
  }

  private BindingToListItems(List list, Preference<ImmutableList<String>> preference) {
    this.list = list;
    this.preference = preference;
  }

  @Override public void applyPreferenceValueToTarget() {
    apply(preference.value());
  }

  @Override public void applyDefaultPreferenceValueToTarget() {
    apply(preference.defaultValue());
  }

  private void apply(Collection<String> value) {
    list.removeAll();
    for (String s : value) {
      list.add(s);
    }
  }

  @Override public void savePreferenceValue() {
    preference.updateValue(copyOf(list.getItems()));
  }

  public static class BindingBuilder {
    private final List list;

    BindingBuilder(List list) {
      this.list = list;
    }

    public BindingToListItems to(Preference<ImmutableList<String>> preference) {
      return new BindingToListItems(list, preference);
    }
  }
}

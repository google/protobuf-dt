/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.ui.preferences.StringSplitter;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PreferenceFactory {
  private final IPreferenceStore store;

  public PreferenceFactory(IPreferenceStore store) {
    this.store = store;
  }

  public Preference<Boolean> newBooleanPreference(String name) {
    return new BooleanPreference(name, store);
  }

  public Preference<String> newStringPreference(String name) {
    return new StringPreference(name, store);
  }

   public Preference<ImmutableList<String>> newStringListPreference(String name, StringSplitter splitter) {
    return new StringListPreference(name, splitter, store);
  }
}

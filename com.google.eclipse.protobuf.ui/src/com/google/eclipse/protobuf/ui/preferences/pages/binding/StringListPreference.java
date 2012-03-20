/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import static org.eclipse.xtext.util.Strings.concat;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.ui.preferences.StringSplitter;

/**
 * A preference that stores a list of {@code String} values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class StringListPreference extends AbstractPreference<ImmutableList<String>> {
  private final StringSplitter splitter;

  StringListPreference(String name, StringSplitter splitter, IPreferenceStore store) {
    super(name, store);
    this.splitter = splitter;
  }

  @Override public ImmutableList<String> value() {
    String value = getPreferenceStore().getString(name());
    return splitter.splitIntoList(value);
  }

  @Override public ImmutableList<String> defaultValue() {
    String defaultValue = getPreferenceStore().getDefaultString(name());
    return splitter.splitIntoList(defaultValue);
  }

  @Override public void updateValue(ImmutableList<String> value) {
    getPreferenceStore().setValue(name(), concatenate(value));
  }

  private String concatenate(List<String> value) {
    return concat(splitter.delimiter(), value);
  }
}

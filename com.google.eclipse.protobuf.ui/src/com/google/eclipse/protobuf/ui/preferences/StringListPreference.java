/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import static org.eclipse.xtext.util.Strings.*;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A preference that stores a list of {@code String} values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class StringListPreference extends Preference<List<String>> {

  private final String delimiter;

  /**
   * Creates a new </code>{@link StringListPreference}</code>.
   * @param name the name of this preference.
   * @param delimiter the delimiter to split a single {@code String} into a list.
   * @param store the store for this preference.
   */
  public StringListPreference(String name, String delimiter, IPreferenceStore store) {
    super(name, store);
    this.delimiter = delimiter;
  }

  /** {@inheritDoc} */
  @Override public List<String> value() {
    return doSplit(store.getString(name));
  }

  /** {@inheritDoc} */
  @Override public List<String> defaultValue() {
    return doSplit(store.getDefaultString(name));
  }

  private List<String> doSplit(String value) {
    return split(value, delimiter);
  }

  /** {@inheritDoc} */
  @Override public void value(List<String> value) {
    store.setValue(name, doConcat(value));
  }

  /** {@inheritDoc} */
  @Override public void defaultValue(List<String> value) {
    store.setDefault(name, doConcat(value));
  }

  private String doConcat(List<String> value) {
    return concat(delimiter, value);
  }
}

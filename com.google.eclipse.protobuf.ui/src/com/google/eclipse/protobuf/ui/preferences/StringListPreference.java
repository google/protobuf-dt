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
  @Override public List<String> getValue() {
    String value = getPreferenceStore().getString(getName());
    return splitIntoList(value);
  }

  /** {@inheritDoc} */
  @Override public List<String> getDefaultValue() {
    String defaultValue = getPreferenceStore().getDefaultString(getName());
    return splitIntoList(defaultValue);
  }

  private List<String> splitIntoList(String value) {
    return split(value, delimiter);
  }

  /** {@inheritDoc} */
  @Override public void setValue(List<String> value) {
    getPreferenceStore().setValue(getName(), concatenate(value));
  }

  /** {@inheritDoc} */
  @Override public void setDefaultValue(List<String> value) {
    getPreferenceStore().setDefault(getName(), concatenate(value));
  }

  private String concatenate(List<String> value) {
    return concat(delimiter, value);
  }
}

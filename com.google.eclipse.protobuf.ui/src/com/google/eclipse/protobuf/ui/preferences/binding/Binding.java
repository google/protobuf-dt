/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.binding;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Control;

/**
 * Binds a value from a <code>{@link IPreferenceStore}</code> to the selection of a
 * <code>{@link Control}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Binding {

  /**
   * Reads a preference from the given <code>{@link IPreferenceStore}</code> and applies it to this binding's
   * <code>{@link Control}</code>.
   * @param store the preference store.
   */
  void read(IPreferenceStore store);

  /**
   * Reads a default value preference from the given <code>{@link IPreferenceStore}</code> and applies it to the this
   * binding's <code>{@link Control}</code>.
   * @param store the preference store.
   */
  void readDefault(IPreferenceStore store);

  /**
   * Applies the value of this binding's <code>{@link Control}</code> to a preference in the given
   * <code>{@link IPreferenceStore}</code>.
   * @param store the preference store.
   */
  void save(IPreferenceStore store);

}

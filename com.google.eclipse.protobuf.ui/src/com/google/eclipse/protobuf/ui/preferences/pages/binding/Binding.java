/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Binds a value from a <code>{@link IPreferenceStore}</code> to an object.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Binding {
  /**
   * Reads a preference value and applies it to the target object.
   */
  void applyPreferenceValueToTarget();

  /**
   * Reads a default value preference and applies it to the the target object.
   */
  void applyDefaultPreferenceValueToTarget();

  /**
   * Saves the value of the target object to a preference.
   */
  void savePreferenceValue();
}

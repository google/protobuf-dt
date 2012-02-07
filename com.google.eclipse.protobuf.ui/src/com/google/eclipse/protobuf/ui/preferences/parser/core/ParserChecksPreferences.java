/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.parser.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * "Parser checks" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ParserChecksPreferences {
  // TODO change name to "googleInternal" or something similar.
  private final BooleanPreference enableProto2OnlyChecks;

  /**
   * Creates a new <code>{@link ParserChecksPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   */
  public ParserChecksPreferences(IPreferenceStoreAccess storeAccess) {
    this(storeAccess.getWritablePreferenceStore());
  }

  /**
   * Creates a new <code>{@link ParserChecksPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public ParserChecksPreferences(IPreferenceStore store) {
    enableProto2OnlyChecks = new BooleanPreference("parser.checkProto2Only", store);
  }

  /**
   * Returns the setting that specifies if "proto2" only files are allowed.
   * @return the setting that specifies if "proto2" only files are allowed.
   */
  public BooleanPreference enableProto2OnlyChecks() {
    return enableProto2OnlyChecks;
  }
}

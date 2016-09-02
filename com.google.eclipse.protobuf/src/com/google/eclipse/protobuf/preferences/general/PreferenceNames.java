/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.preferences.general;

/** Preference names for descriptor proto preferences. */
public final class PreferenceNames {
  /**
   * Default value for the preference {@literal general.descriptorPath}.
   */
  public static final String DEFAULT_DESCRIPTOR_PATH = "google/protobuf/descriptor.proto";

  /**
   * Preference storing the import path of the options definition file.
   */
  public static final String DESCRIPTOR_PROTO_PATH = "general.descriptorPath";

  /**
   * Preference determining whether the project specific settings for the are enabled.
   */
  public static final String ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME =
      "general.enableProjectSettings";

  /**
   * Preference determining whether protocol buffer files are validated on activation.
   */
  public static final String VALIDATE_FILES_ON_ACTIVATION = "general.validateFilesOnActivation";

  private PreferenceNames() {}
}

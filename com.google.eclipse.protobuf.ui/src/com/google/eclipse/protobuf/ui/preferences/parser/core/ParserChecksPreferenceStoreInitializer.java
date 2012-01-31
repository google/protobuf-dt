/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.parser.core;

import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * Initializes default values for the "parser checks" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ParserChecksPreferenceStoreInitializer implements IPreferenceStoreInitializer {
  @Override public void initialize(IPreferenceStoreAccess storeAccess) {
    ParserChecksPreferences preferences = new ParserChecksPreferences(storeAccess);
    preferences.enableProto2OnlyChecks().setDefaultValue(true);
  }
}

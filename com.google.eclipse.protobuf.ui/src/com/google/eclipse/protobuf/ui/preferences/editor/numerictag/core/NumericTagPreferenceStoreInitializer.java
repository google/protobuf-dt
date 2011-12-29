/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.numerictag.core;

import static java.util.Collections.singletonList;

import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * Initializes default values for <code>{@link NumericTagPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NumericTagPreferenceStoreInitializer implements IPreferenceStoreInitializer {
  @Override public void initialize(IPreferenceStoreAccess storeAccess) {
    NumericTagPreferences preferences = new NumericTagPreferences(storeAccess);
    preferences.patterns().setDefaultValue(singletonList("Next[\\s]+Id:[\\s]+[\\d]+"));
  }
}

/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.preferences;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.osgi.service.prefs.BackingStoreException;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A preference store initializer that does not overwrite defaults that have already been set.
 */
public abstract class DefaultPreservingInitializer implements IPreferenceStoreInitializer {
  @Inject
  @Named(Constants.LANGUAGE_NAME)
  private String preferenceScope;

  private IPreferenceStore store;
  private Set<String> alreadySetDefaults = new HashSet<>();

  private static Logger LOG = Logger.getLogger(DefaultPreservingInitializer.class.getName());

  @Override
  public final void initialize(IPreferenceStoreAccess access) {
    this.store = access.getWritablePreferenceStore();
    try {
      for (String key : DefaultScope.INSTANCE.getNode(preferenceScope).keys()) {
        alreadySetDefaults.add(key);
      }
    } catch (BackingStoreException e) {
      LOG.log(Level.SEVERE, "Unable to get already set defaults for " + preferenceScope, e);
    }
    setDefaults();
  }

  public abstract void setDefaults();

  public void setDefault(String preference, double value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }

  public void setDefault(String preference, float value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }

  public void setDefault(String preference, int value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }

  public void setDefault(String preference, long value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }

  public void setDefault(String preference, String value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }

  public void setDefault(String preference, boolean value) {
    if (!alreadySetDefaults.contains(preference)) {
      store.setDefault(preference, value);
    }
  }
}

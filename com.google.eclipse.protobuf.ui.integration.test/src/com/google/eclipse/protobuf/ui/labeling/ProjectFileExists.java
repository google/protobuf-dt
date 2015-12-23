/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProjectFileExists extends TypeSafeMatcher<String> {
  private static final ProjectFileExists INSTANCE = new ProjectFileExists();

  static ProjectFileExists existsInProject() {
    return INSTANCE;
  }

  private ProjectFileExists() {}

  @Override public boolean matchesSafely(String item) {
    URL entry = ProtobufActivator.getInstance().getBundle().getEntry("icons/" + item);
    if (entry == null) {
      return false;
    }
    try {
      String entryPath = FileLocator.resolve(entry).getFile();
      File file = new File(entryPath);
      return file.isFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override public void describeTo(Description description) {}
}

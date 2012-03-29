/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import org.eclipse.core.resources.IProject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface CodeGenerationPreference {
  boolean isEnabled();

  String outputDirectory();

  IProject project();
}

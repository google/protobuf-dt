/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static com.google.eclipse.protobuf.scoping.ProtoDescriptor.DESCRIPTOR_IMPORT_URI;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtoDescriptorPathFinder {

  private static final String DESCRIPTOR_FQN = "/" + DESCRIPTOR_IMPORT_URI;

  public String findRootOf(String descriptorFilePath) {
    if (descriptorFilePath == null) return null;
    int indexOfDescriptorFqn = descriptorFilePath.indexOf(DESCRIPTOR_FQN);
    if (indexOfDescriptorFqn == -1) {
      String format = "Path '%s' does not contain '%s'";
      throw new IllegalArgumentException(String.format(format, descriptorFilePath, DESCRIPTOR_FQN));
    }
    return descriptorFilePath.substring(0, indexOfDescriptorFqn);
  }
}

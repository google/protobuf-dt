/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static java.io.File.separator;
import static java.util.Arrays.asList;
import static org.eclipse.xtext.util.Strings.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtoDescriptorPathFinder {

  private static final String DESCRIPTOR_FQN = concat(separator, asList("", "google", "protobuf", "descriptor.proto"));

  public String findRootOf(String descriptorFilePath) {
    if (isEmpty(descriptorFilePath)) return null;
    int indexOfDescriptorFqn = descriptorFilePath.indexOf(DESCRIPTOR_FQN);
    if (indexOfDescriptorFqn == -1) {
      String format = "Path '%s' does not contain '%s'";
      throw new IllegalArgumentException(String.format(format, descriptorFilePath, DESCRIPTOR_FQN));
    }
    return descriptorFilePath.substring(0, indexOfDescriptorFqn);
  }
}

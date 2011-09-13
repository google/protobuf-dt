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

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class ProtoDescriptorPathFinder {

  private final String descriptorFqn;
  
  ProtoDescriptorPathFinder() {
    this(separator);
  }
  
  ProtoDescriptorPathFinder(String fileSeparator) {
    descriptorFqn = concat(separator, asList("", "google", "protobuf", "descriptor.proto"));
  }
    
  String findRootOf(String descriptorFilePath) {
    if (isEmpty(descriptorFilePath)) return null;
    int indexOfDescriptorFqn = descriptorFilePath.indexOf(descriptorFqn);
    if (indexOfDescriptorFqn == -1) {
      String format = "Path '%s' does not contain '%s'";
      throw new IllegalArgumentException(String.format(format, descriptorFilePath, descriptorFqn));
    }
    return descriptorFilePath.substring(0, indexOfDescriptorFqn);
  }
}

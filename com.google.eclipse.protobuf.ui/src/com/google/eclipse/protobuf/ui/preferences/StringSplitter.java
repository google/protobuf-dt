/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import static org.eclipse.xtext.util.Strings.split;

import static com.google.common.collect.ImmutableList.copyOf;

import com.google.common.collect.ImmutableList;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class StringSplitter {
  private final String delimiter;

  public StringSplitter(String delimiter) {
    this.delimiter = delimiter;
  }

  public ImmutableList<String> splitIntoList(String value) {
    return copyOf(split(value, delimiter));
  }

  public String delimiter() {
    return delimiter;
  }
}

/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import com.google.inject.Inject;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufTerminalConverters extends DefaultTerminalConverters {
  @Inject private DOUBLEValueConverter doubleValueConverter;
  @Inject private HEXValueConverter hexValueConverter;
  @Inject private LONGValueConverter longValueConverter;

  @ValueConverter(rule = "DOUBLE")
  public IValueConverter<Double> DOUBLE() {
    return doubleValueConverter;
  }

  @ValueConverter(rule = "HEX")
  public IValueConverter<Long> HEX() {
    return hexValueConverter;
  }

  @ValueConverter(rule = "LONG")
  public IValueConverter<Long> LONG() {
    return longValueConverter;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.ui.validation.MarkerTypes.EDITOR_CHECK;

import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class ProtobufMarkerTypeProvider extends MarkerTypeProvider {
  @Override public String getMarkerType(Issue issue) {
    return EDITOR_CHECK;
  }

  @Override public CheckType getCheckType(String markerType) {
    return CheckType.FAST;
  }
}

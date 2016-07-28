/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.impl.CaseInsensitivityHelper;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION_SOURCE__TARGET;

/**
 * Specify cases where scoping is case insensitive.
 *
 * @author atrookey@google.com (Alexander Rookey)
 */
public class ProtobufCaseInsensitivityHelper extends CaseInsensitivityHelper {
  @Override
  public boolean isIgnoreCase(EReference reference) {
    if (reference.equals(OPTION_SOURCE__TARGET)) {
      return true;
    }
    return super.isIgnoreCase(reference);
  }
}

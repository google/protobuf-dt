/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.protobuf.Name;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Name}</code>s.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Names {
  
  public String valueOf(Name name) {
    if (name == null) return null;
    return name.getValue();
  }
  
}

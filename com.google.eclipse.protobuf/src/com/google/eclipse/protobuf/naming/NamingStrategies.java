/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import com.google.eclipse.protobuf.model.util.Options;
import com.google.inject.*;

/**
 * Utility methods related to naming.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class NamingStrategies {
  private final NamingStrategy normal;
  private final NamingStrategy option;

  @Inject public NamingStrategies(NameResolver nameResolver, Options options) {
    normal = new NormalNamingStrategy(nameResolver);
    option = new OptionNamingStrategy(nameResolver, options);
  }

  NamingStrategy normal() {
    return normal;
  }

  NamingStrategy option() {
    return option;
  }
}

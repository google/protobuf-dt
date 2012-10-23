/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import org.eclipse.xtext.resource.IEObjectDescription;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class UniqueDescriptions {

  private final Map<String, IEObjectDescription> descriptionsByName = newHashMap();

  void addAll(Collection<IEObjectDescription> descriptions) {
    for (IEObjectDescription description : descriptions) {
      String name = description.getName().toString();
      if (!descriptionsByName.containsKey(name)) {
        descriptionsByName.put(name, description);
      }
    }
  }

  Collection<IEObjectDescription> values() {
    return descriptionsByName.values();
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.outline;

import static com.google.inject.internal.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableList;

import java.util.*;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class OutlineViewModel {

  private static final Class<?>[] GROUP_TYPES = { Package.class, Import.class, ProtobufElement.class };

  private final Map<Class<?>, List<EObject>> elements = newLinkedHashMap();

  OutlineViewModel(Protobuf protobuf) {
    initialize();
    for (ProtobufElement e : protobuf.getElements()) {
      for (Class<?> type : GROUP_TYPES) {
        if (!type.isInstance(e)) {
          continue;
        }
        elements.get(type).add(e);
        break;
      }
    }
  }

  private void initialize() {
    for (Class<?> type : GROUP_TYPES) {
      elements.put(type, new ArrayList<EObject>());
    }
  }

  List<EObject> packages() {
    return elementsOfType(Package.class);
  }

  List<EObject> imports() {
    return elementsOfType(Import.class);
  }

  List<EObject> remainingElements() {
    return elementsOfType(ProtobufElement.class);
  }

  private List<EObject> elementsOfType(Class<?> type) {
    return unmodifiableList(elements.get(type));
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableCollection;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class IEObjectDescriptionChooser {
  Collection<IEObjectDescription> shortestQualifiedNamesIn(Collection<IEObjectDescription> descriptions) {
    if (descriptions.isEmpty()) {
      return emptySet();
    }
    Map<EObject, IEObjectDescription> shortestOnes = newHashMap();
    for (IEObjectDescription d : descriptions) {
      EObject e = d.getEObjectOrProxy();
      IEObjectDescription stored = shortestOnes.get(e);
      if (stored != null) {
        QualifiedName currentName = d.getName();
        if (currentName.getSegmentCount() >= stored.getName().getSegmentCount()) {
          continue;
        }
      }
      shortestOnes.put(e, d);
    }
    return unmodifiableCollection(shortestOnes.values());
  }
}

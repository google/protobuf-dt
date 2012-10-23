/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static java.util.Collections.unmodifiableList;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.XtextResource;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class Resources {

  static List<EObject> eObjects(XtextResource resource, List<URI> uris) {
    List<EObject> found = newArrayList();
    for (URI uri : uris) {
      found.add(resource.getEObject(uri.fragment()));
    }
    return unmodifiableList(found);
  }

  private Resources() {}
}

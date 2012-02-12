/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.cdt.matching.ContentsByType.contentsOf;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.protobuf.Message;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MessageMatcherStrategy implements ProtobufElementMatcherStrategy {
  @Override public List<URI> matchingProtobufElementLocations(ContentsByType contents, String[] qualifiedNameSegments) {
    List<URI> matches = newArrayList();
    int segmentCount = qualifiedNameSegments.length;
    for (int i = 0; i < segmentCount; i++) {
      String segment = qualifiedNameSegments[i];
      for (EObject e : contents.ofType(supportedType())) {
        if (!(e instanceof Message)) {
          continue;
        }
        Message message = (Message) e;
        if (segment.equals(message.getName())) {
          if (i == segmentCount - 1) {
            // we found what we were looking for.
            matches.add(uriOf(message));
            break;
          }
          // go one level deeper.
          contents = contentsOf(message);
          continue;
        }
        if (segment.contains("_")) {

        }
      }
    }
    return unmodifiableList(matches);
  }

  private URI uriOf(EObject e) {
    Resource resource = e.eResource();
    URI uri = resource.getURI();
    uri = uri.appendFragment(resource.getURIFragment(e));
    return uri;
  }

  @Override public EClass supportedType() {
    return MESSAGE;
  }
}

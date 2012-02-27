/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;

import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MessageMatcherStrategy implements ProtobufElementMatcherStrategy {
  @Inject private ModelObjects modelObjects;

  @Override public List<URI> matchingProtobufElementLocations(EObject root, List<String> qualifiedName) {
    List<URI> matches = newArrayList();
    List<EObject> contents = root.eContents();
    while (!qualifiedName.isEmpty()) {
      String segment = qualifiedName.remove(0);
      for (EObject o : contents) {
        if (!isSupported(o)) {
          continue;
        }
        Message message = (Message) o;
        if (equal(message.getName(), segment)) {
          if (qualifiedName.isEmpty()) {
            // this is the last segment. This message is a perfect match.
            matches.add(modelObjects.uriOf(message));
          } else {
            // keep looking for match.
            contents = message.eContents();
          }
          break;
        }
        if (segment.contains("_")) {
          matches.addAll(matchingNestedElementLocations(contents, segment));
          break;
        }
      }
    }
    return unmodifiableList(matches);
  }

  private List<URI> matchingNestedElementLocations(List<EObject> elements, String nestedQualifiedName) {
    List<URI> matches = newArrayList();
    for (EObject o : elements) {
      if (!isSupported(o)) {
        continue;
      }
      Message message = (Message) o;
      String messageName = message.getName();
      if (nestedQualifiedName.startsWith(messageName)) {
        String rest = nestedQualifiedName.substring(messageName.length());
        if (rest.isEmpty()) {
          matches.add(modelObjects.uriOf(message));
        }
        else {
          if (rest.startsWith("_")) {
            rest = rest.substring(1);
          }
          matches.addAll(matchingNestedElementLocations(message.eContents(), rest));
        }
        break;
      }
    }
    return matches;
  }

  private boolean isSupported(EObject o) {
    return supportedType().equals(o.eClass());
  }

  @Override public EClass supportedType() {
    return MESSAGE;
  }
}

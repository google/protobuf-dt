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

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.cdt.util.ExtendedListIterator.newIterator;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.cdt.util.ExtendedIterator;
import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MessageMatcherStrategy extends AbstractProtobufElementMatcherStrategy {
  @Inject private ModelObjects modelObjects;

  @Override public List<URI> matchingProtobufElementLocations(EObject root, ExtendedIterator<String> qualifiedName) {
    List<URI> matches = newArrayList();
    ExtendedIterator<EObject> contents = newIterator(root.eContents());
    while (qualifiedName.hasNext()) {
      String segment = qualifiedName.next();
      while (contents.hasNext()) {
        EObject o = contents.next();
        if (!isSupported(o)) {
          continue;
        }
        Message message = (Message) o;
        if (equal(message.getName(), segment)) {
          if (qualifiedName.wasLastListElementRetrieved()) {
            // this is the last segment. This message is a perfect match.
            matches.add(modelObjects.uriOf(message));
          } else {
            // keep looking for match.
            matches.addAll(matchingProtobufElementLocations(message, qualifiedName.notRetrievedYet()));
          }
        }
        if (segment.contains(NESTED_ELEMENT_SEPARATOR)) {
          List<Message> nestedMessages = matchingNestedMessages(message, segment);
          if (qualifiedName.wasLastListElementRetrieved()) {
            for (Message m : nestedMessages) {
              matches.add(modelObjects.uriOf(m));
            }
          } else {
            for (Message m : nestedMessages) {
              matches.addAll(matchingProtobufElementLocations(m, qualifiedName.notRetrievedYet()));
            }
          }
        }
      }
    }
    return unmodifiableList(matches);
  }

  private List<Message> matchingNestedMessages(Message root, String qualifiedName) {
    List<Message> matches = newArrayList();
    String messageName = root.getName();
    if (qualifiedName.startsWith(messageName)) {
      String rest = qualifiedName.substring(messageName.length());
      if (rest.isEmpty()) {
        matches.add(root);
      }
      else {
        if (rest.startsWith(NESTED_ELEMENT_SEPARATOR)) {
          rest = rest.substring(1);
        }
        for (EObject o : root.eContents()) {
          if (!isSupported(o)) {
            continue;
          }
          matches.addAll(matchingNestedMessages((Message) o, rest));
        }
      }
    }
    return matches;
  }

  @Override public EClass supportedType() {
    return MESSAGE;
  }
}

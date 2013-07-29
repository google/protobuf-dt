/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.unmodifiableList;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.emf.ecore.EObject;

/**
 * Utility methods related to <code>{@link Message}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Messages {
  @Inject private ModelObjects modelObjects;
  @Inject private TypeExtensions typeExtensions;

  /**
   * Returns all the extensions of the given message declared in the same file as the message.
   * @param message the given message.
   * @return all the extensions of the given message declared in the same file as the message, or an empty collection if
   * none are found.
   */
  public Collection<TypeExtension> localExtensionsOf(Message message) {
    return extensionsOf(message, modelObjects.rootOf(message));
  }

  public Collection<TypeExtension> extensionsOf(Message message, Protobuf root) {
    Set<TypeExtension> extensions = newHashSet();
    for (TypeExtension extension : getAllContentsOfType(root, TypeExtension.class)) {
      Message referred = typeExtensions.messageFrom(extension);
      if (message.equals(referred)) {
        extensions.add(extension);
      }
    }
    return extensions;
  }

  /**
   * Returns all the fields of the given <code>{@link Message}</code>.
   * @param message the given message.
   * @return all the fields of the given {@code Message}.
   */
  public Collection<MessageField> fieldsOf(Message message) {
    List<MessageField> fields = newArrayList();
    fieldsOf(message, fields);
    return unmodifiableList(fields);
  }

  private void fieldsOf(EObject message, List<MessageField> fields) {
    if (message instanceof Message) {
      for (MessageElement e : ((Message) message).getElements()) {
        if (e instanceof MessageField) {
          fields.add((MessageField) e);
        }
      }
    } else if (message instanceof OneOf) {
      for (MessageElement e : ((OneOf) message).getElements()) {
        if (e instanceof MessageField) {
          fields.add((MessageField) e);
        }
      }
    }
  }
}

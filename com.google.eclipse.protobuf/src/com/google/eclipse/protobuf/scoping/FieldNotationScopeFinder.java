/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FieldNotationScopeFinder {
  @Inject private MessageFields messageFields;
  @Inject private Messages messages;
  @Inject private ModelObjects modelObjects;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  Collection<IEObjectDescription> sourceOfNormalFieldNamesOf(ComplexValue value) {
    MessageField source = sourceOf(value);
    if (source == null) {
      return emptySet();
    }
    return propertiesInTypeOf(source);
  }

  Collection<IEObjectDescription> sourceOfExtensionFieldNamesOf(ComplexValue value) {
    MessageField source = sourceOf(value);
    if (source == null) {
      return emptySet();
    }
    return propertiesInExtendMessageOf(source);
  }

  private MessageField sourceOf(ComplexValue value) {
    IndexedElement source = null;
    EObject container = value.eContainer();
    if (container instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) container;
      source = options.sourceOf(option);
    }
    if (container instanceof ComplexValueField) {
      source = sourceOf((ComplexValueField) container);
    }
    return (source instanceof MessageField) ? (MessageField) source : null;
  }

  private MessageField sourceOf(ComplexValueField field) {
    FieldName name = field.getName();
    return (name == null) ? null : name.getTarget();
  }

  private Collection<IEObjectDescription> propertiesInTypeOf(MessageField field) {
    Set<IEObjectDescription> descriptions = newHashSet();
    Message fieldType = messageFields.messageTypeOf(field);
    for (MessageElement element : fieldType.getElements()) {
      if (element instanceof MessageField) {
        String name = ((MessageField) element).getName();
        descriptions.add(create(name, element));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> propertiesInExtendMessageOf(MessageField field) {
    Set<IEObjectDescription> descriptions = newHashSet();
    Message fieldType = messageFields.messageTypeOf(field);
    // check first in descriptor.proto
    for (TypeExtension extension : messages.extensionsOf(fieldType, modelObjects.rootOf(field))) {
      for (MessageElement element : extension.getElements()) {
        if (!(element instanceof MessageField)) {
          continue;
        }
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(element));
      }
    }
    return descriptions;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptySet;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FieldNotationScopeFinder {

  @Inject private FieldOptions fieldOptions;
  @Inject private Options options;
  @Inject private ModelFinder modelFinder;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  Collection<IEObjectDescription> sourceOf(FieldNotationNameSource s) {
    EObject container = s.eContainer();
    if (!(container instanceof AggregateField)) return emptySet();
    MessageField field = sourceOf((AggregateField) container);
    if (field == null) return emptySet();
    if (s instanceof NormalFieldNotationNameSource) {
      return propertiesInTypeOf(field);
    }
    return propertiesInExtendMessageOf(field);
  }
  
  private MessageField sourceOf(AggregateField field) {
    EObject container = field.eContainer();
    IndexedElement source = null;
    if (container instanceof AggregateValue) {
      container = container.eContainer();
      if (container instanceof CustomOption) {
        CustomOption option = (CustomOption) container;
        source = options.sourceOf(option);
      }
      if (container instanceof CustomFieldOption) {
        CustomFieldOption option = (CustomFieldOption) container;
        source = fieldOptions.sourceOf(option);
      }
      if (container instanceof ComplexFieldNotation) {
        ComplexFieldNotation complex = (ComplexFieldNotation) container;
        return sourceOf(complex);
      }
    }
    return ((source instanceof MessageField) ? (MessageField) source : null);
  }

  private MessageField sourceOf(ComplexFieldNotation n) {
    FieldNotationNameSource s = n.getName();
    if (s instanceof NormalFieldNotationNameSource) {
      NormalFieldNotationNameSource normal = (NormalFieldNotationNameSource) s;
      return normal.getProperty();
    }
    if (s instanceof ExtensionFieldNotationNameSource) {
      ExtensionFieldNotationNameSource normal = (ExtensionFieldNotationNameSource) s;
      return normal.getExtension();
    }
    return null;
  }
  
  private Collection<IEObjectDescription> propertiesInTypeOf(MessageField field) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Message fieldType = modelFinder.messageTypeOf(field);
    for (MessageElement element : fieldType.getElements()) {
      if (element instanceof MessageField) {
        String name = ((MessageField) element).getName();
        descriptions.add(create(name, element));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> propertiesInExtendMessageOf(MessageField field) {
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Message fieldType = modelFinder.messageTypeOf(field);
    // check first in descriptor.proto
    for (MessageExtension extension : modelFinder.extensionsOf(fieldType, modelFinder.rootOf(field))) {
      for (MessageElement element : extension.getElements()) {
        if (!(element instanceof MessageField)) continue;
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(element));
      }
    }
    return descriptions;
  }
}

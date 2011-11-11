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

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FieldNotationScopeFinder {

  @Inject private FieldOptions fieldOptions;
  @Inject private Options options;
  @Inject private ModelFinder modelFinder;

  Collection<IEObjectDescription> sourceOf(FieldNotation notation) {
    Property p = source(notation);
    return propertiesInTypeOf(p);
  }
  
  private Property source(FieldNotation notation) {
    EObject container = notation.eContainer();
    IndexedElement source = null;
    if (container instanceof MessageNotation) {
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
        FieldNotationNameSource name = complex.getName();
        return sourceOf(name);
      }
    }
    return ((source instanceof Property) ? (Property) source : null);
  }

  private Property sourceOf(FieldNotationNameSource s) {
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
  
  private Collection<IEObjectDescription> propertiesInTypeOf(Property p) {
    if (p == null) return emptySet();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    Message propertyType = modelFinder.messageTypeOf(p);
    for (MessageElement element : propertyType.getElements()) {
      if (element instanceof Property) {
        String name = ((Property) element).getName();
        descriptions.add(create(name, element));
      }
    }
    return descriptions;
  }
}

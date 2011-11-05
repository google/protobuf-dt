/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.*;
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
class CustomOptionFieldScopeFinder {

  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder modelFinder;
  @Inject private OptionFields optionFields;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  Collection<IEObjectDescription> findScope(OptionMessageFieldSource fieldSource) {
    return findScope(fieldSource, new IEObjectDescriptionsProvider() {
      @Override public Collection<IEObjectDescription> fieldsInType(Field f) {
        if (!(f instanceof Property)) return emptyList();
        Message propertyType = modelFinder.messageTypeOf((Property) f);
        if (propertyType == null) return emptyList();
        Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
        for (MessageElement e : propertyType.getElements()) {
          if (!(e instanceof Property)) continue;
          Property current = (Property) e;
          descriptions.add(create(current.getName(), current));
        }
        return descriptions;
      }
    });
  }

  Collection<IEObjectDescription> findScope(OptionExtendMessageFieldSource fieldSource) {
    return findScope(fieldSource, new IEObjectDescriptionsProvider() {
      @Override public Collection<IEObjectDescription> fieldsInType(Field f) {
        if (!(f instanceof Property)) return emptyList();
        Message propertyType = modelFinder.messageTypeOf((Property) f);
        if (propertyType == null) return emptyList();
        Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
        for (ExtendMessage extend : modelFinder.extensionsOf(propertyType)) {
          for (MessageElement e : extend.getElements()) {
            if (!(e instanceof Property)) continue;
            Property current = (Property) e;
            descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(current));
            descriptions.add(create(current.getName(), current));
          }
        }
        return descriptions;
      }
    });
  }

  private Collection<IEObjectDescription> findScope(EObject fieldSource, IEObjectDescriptionsProvider provider) {
    EObject container = fieldSource.eContainer();
    Field f = null;
    if (container instanceof CustomOption) {
      CustomOption option = (CustomOption) container;
      f = referredField(fieldSource, option);
    }
    if (container instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) container;
      f = referredField(fieldSource, option);
    }
    if (f != null) return provider.fieldsInType(f);
    return emptySet();
  }

  private Field referredField(EObject fieldSource, final CustomOption option) {
    return referredField(fieldSource, option.getOptionFields(), new Provider<Field>() {
      @Override public Field get() {
        return options.sourceOf(option);
      }
    });
  }

  private Field referredField(EObject fieldSource, final CustomFieldOption option) {
    return referredField(fieldSource, option.getOptionFields(), new Provider<Field>() {
      @Override public Field get() {
        return fieldOptions.sourceOf(option);
      }
    });
  }
  
  private Field referredField(EObject fieldSource, List<OptionFieldSource> allFieldSources, 
      Provider<Field> provider) {
    OptionFieldSource previous = null;
    boolean isFirstField = true;
    for (OptionFieldSource s : allFieldSources) {
      if (s == fieldSource) {
        return (isFirstField) ? provider.get() : optionFields.sourceOf(previous);
      }
      previous = s;
      isFirstField = false;
    }
    return null;
  }
  
  private static interface IEObjectDescriptionsProvider {
    Collection<IEObjectDescription> fieldsInType(Field f);
  }
}

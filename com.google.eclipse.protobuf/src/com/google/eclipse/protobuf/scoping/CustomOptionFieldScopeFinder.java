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
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  Collection<IEObjectDescription> findScope(MessagePropertyRef ref) {
    return findScope(ref, new IEObjectDescriptionsProvider() {
      @Override public Collection<IEObjectDescription> fieldsInType(Property p) {
        Message propertyType = modelFinder.messageTypeOf(p);
        if (propertyType == null) return emptyList();
        Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
        for (MessageElement e : propertyType.getElements()) {
          if (!(e instanceof Property)) continue;
          Property f = (Property) e;
          descriptions.add(create(f.getName(), f));
        }
        return descriptions;
      }
    });
  }

  Collection<IEObjectDescription> findScope(ExtendMessagePropertyRef ref) {
    return findScope(ref, new IEObjectDescriptionsProvider() {
      @Override public Collection<IEObjectDescription> fieldsInType(Property p) {
        Message propertyType = modelFinder.messageTypeOf(p);
        if (propertyType == null) return emptyList();
        Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
        for (ExtendMessage extend : modelFinder.extensionsOf(propertyType)) {
          for (MessageElement e : extend.getElements()) {
            if (!(e instanceof Property)) continue;
            Property f = (Property) e;
            descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(f));
            descriptions.add(create(f.getName(), f));
          }
        }
        return descriptions;
      }
    });
  }

  private Collection<IEObjectDescription> findScope(EObject ref, IEObjectDescriptionsProvider provider) {
    EObject container = ref.eContainer();
    Property p = null;
    if (container instanceof CustomOption) {
      CustomOption option = (CustomOption) container;
      p = referredProperty(ref, option);
    }
    if (container instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) container;
      p = referredProperty(ref, option);
    }
    if (p != null) return provider.fieldsInType(p);
    return emptySet();
  }

  private Property referredProperty(EObject ref, final CustomOption option) {
    return referredProperty(ref, option.getOptionFields(), new Provider<Property>() {
      @Override public Property get() {
        return options.propertyFrom(option);
      }
    });
  }

  private Property referredProperty(EObject ref, final CustomFieldOption option) {
    return referredProperty(ref, option.getOptionFields(), new Provider<Property>() {
      @Override public Property get() {
        return fieldOptions.propertyFrom(option);
      }
    });
  }
  
  private Property referredProperty(EObject ref, List<OptionField> fields, Provider<Property> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField field : fields) {
      if (field == ref) {
        return (isFirstField) ? provider.get() : propertyFrom(previous);
      }
      previous = field;
      isFirstField = false;
    }
    return null;
  }

  private Property propertyFrom(OptionField field) {
    if (field instanceof MessagePropertyRef) {
      MessagePropertyRef ref = (MessagePropertyRef) field;
      return ref.getMessageProperty();
    }
    if (field instanceof ExtendMessagePropertyRef) {
      ExtendMessagePropertyRef ref = (ExtendMessagePropertyRef) field;
      return ref.getExtendMessageProperty();
    }
    return null;
  }
  
  private static interface IEObjectDescriptionsProvider {
    Collection<IEObjectDescription> fieldsInType(Property p);
  }
}

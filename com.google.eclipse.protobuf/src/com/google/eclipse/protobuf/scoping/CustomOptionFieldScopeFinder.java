/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.naming.Naming.NameTarget.OPTION;
import static java.util.Collections.*;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionFieldScopeFinder {

  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder modelFinder;
  @Inject private OptionFields optionFields;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  Collection<IEObjectDescription> findScope(CustomOption option, OptionMessageFieldSource source) {
    return findScope(option, source, new MessageFieldDescriptorProvider());
  }

  private Collection<IEObjectDescription> findScope(final CustomOption option, EObject source,
      IEObjectDescriptionsProvider provider) {
    IndexedElement e = referredField(source, option.getOptionFields(), new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return options.sourceOf(option);
      }
    });
    if (e != null) return provider.fieldsInType(e);
    return emptySet();
  }

  Collection<IEObjectDescription> findScope(CustomFieldOption option, OptionMessageFieldSource source) {
    return findScope(option, source, new MessageFieldDescriptorProvider());
  }

  private Collection<IEObjectDescription> findScope(final CustomFieldOption option, EObject source,
      IEObjectDescriptionsProvider provider) {
    IndexedElement e = referredField(source, option.getOptionFields(), new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return fieldOptions.sourceOf(option);
      }
    });
    if (e != null) return provider.fieldsInType(e);
    return emptySet();
  }

  Collection<IEObjectDescription> findScope(CustomOption option, OptionExtendMessageFieldSource source) {
    return findScope(option, source, new ExtendMessageFieldDescriptorProvider());
  }

  Collection<IEObjectDescription> findScope(CustomFieldOption option, OptionExtendMessageFieldSource source) {
    return findScope(option, source, new ExtendMessageFieldDescriptorProvider());
  }

  private IndexedElement referredField(EObject source, List<OptionFieldSource> allFieldSources,
      Provider<IndexedElement> provider) {
    OptionFieldSource previous = null;
    boolean isFirstField = true;
    for (OptionFieldSource s : allFieldSources) {
      if (s == source) {
        return (isFirstField) ? provider.get() : optionFields.sourceOf(previous);
      }
      previous = s;
      isFirstField = false;
    }
    if (source == null) {
      if (previous == null) return provider.get();
      return optionFields.sourceOf(previous);
    }
    return null;
  }

  private class MessageFieldDescriptorProvider implements IEObjectDescriptionsProvider {
    @Override public Collection<IEObjectDescription> fieldsInType(IndexedElement e) {
      Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
      if (e instanceof Property) {
        Message propertyType = modelFinder.messageTypeOf((Property) e);
        for (MessageElement element : propertyType.getElements()) {
          IEObjectDescription d = describe(element);
          if (d != null) descriptions.add(d);
        }
      }
      if (e instanceof Group) {
        for (GroupElement element : ((Group) e).getElements()) {
          IEObjectDescription d = describe(element);
          if (d != null) descriptions.add(d);
        }
      }
      return descriptions;
    }

    private IEObjectDescription describe(EObject e) {
      if (!(e instanceof IndexedElement)) return null;
      String name = options.nameForOption((IndexedElement) e);
      return create(name, e);
    }
  }

  private class ExtendMessageFieldDescriptorProvider implements IEObjectDescriptionsProvider {
    @Override public Collection<IEObjectDescription> fieldsInType(IndexedElement e) {
      if (!(e instanceof Property)) return emptyList();
      Message propertyType = modelFinder.messageTypeOf((Property) e);
      if (propertyType == null) return emptyList();
      Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
      for (ExtendMessage extend : modelFinder.extensionsOf(propertyType)) {
        for (MessageElement element : extend.getElements()) {
          if (!(element instanceof Property)) continue;
          Property current = (Property) element;
          descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(current, OPTION));
          descriptions.add(create(current.getName(), current));
        }
      }
      return descriptions;
    }
  }

  private static interface IEObjectDescriptionsProvider {
    Collection<IEObjectDescription> fieldsInType(IndexedElement e);
  }
}

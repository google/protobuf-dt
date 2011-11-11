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
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  Collection<IEObjectDescription> findScope(CustomOption option, OptionMessageFieldSource source) {
    return findScope(option, source, new MessageFieldDescriptorProvider());
  }

  private Collection<IEObjectDescription> findScope(final CustomOption option, EObject source,
      IEObjectDescriptionsProvider provider) {
    IndexedElement e = referredField(source, option.getOptionFields(), new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return options.rootSourceOf(option);
      }
    });
    if (e != null) return provider.fieldsInTypeOf(e);
    return emptySet();
  }

  Collection<IEObjectDescription> findScope(CustomFieldOption option, OptionMessageFieldSource source) {
    return findScope(option, source, new MessageFieldDescriptorProvider());
  }

  Collection<IEObjectDescription> findScope(CustomOption option, OptionExtendMessageFieldSource source) {
    return findScope(option, source, new ExtendMessageFieldDescriptorProvider());
  }

  Collection<IEObjectDescription> findScope(CustomFieldOption option, OptionExtendMessageFieldSource source) {
    return findScope(option, source, new ExtendMessageFieldDescriptorProvider());
  }

  private Collection<IEObjectDescription> findScope(final CustomFieldOption option, EObject source,
      IEObjectDescriptionsProvider provider) {
    IndexedElement e = referredField(source, option.getOptionFields(), new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return fieldOptions.rootSourceOf(option);
      }
    });
    if (e != null) return provider.fieldsInTypeOf(e);
    return emptySet();
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
    @Override public Collection<IEObjectDescription> fieldsInTypeOf(IndexedElement e) {
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
    @Override public Collection<IEObjectDescription> fieldsInTypeOf(IndexedElement e) {
      if (!(e instanceof Property)) return emptyList();
      Message propertyType = modelFinder.messageTypeOf((Property) e);
      if (propertyType == null) return emptyList();
      Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
      for (ExtendMessage extend : modelFinder.localExtensionsOf(propertyType)) {
        for (MessageElement element : extend.getElements()) {
          if (!(element instanceof IndexedElement)) continue;
          IndexedElement current = (IndexedElement) element;
          descriptions.addAll(qualifiedNameDescriptions.qualifiedNamesForOption(current));
          String name = options.nameForOption(current);
          descriptions.add(create(name, current));
        }
      }
      return descriptions;
    }
  }

  private static interface IEObjectDescriptionsProvider {
    Collection<IEObjectDescription> fieldsInTypeOf(IndexedElement e);
  }
}

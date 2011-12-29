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

  @Inject private ModelFinder modelFinder;
  @Inject private OptionFields optionFields;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  Collection<IEObjectDescription> findScope(AbstractCustomOption option, MessageOptionField field) {
    return findScope(option, field, new MessageFieldDescriptorProvider());
  }

  Collection<IEObjectDescription> findScope(AbstractCustomOption option, ExtensionOptionField field) {
    return findScope(option, field, new ExtensionFieldDescriptorProvider());
  }

  private Collection<IEObjectDescription> findScope(final AbstractCustomOption option, OptionField field,
      IEObjectDescriptionsProvider provider) {
    IndexedElement e = referredField(option, field, new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return options.rootSourceOf((AbstractOption) option);
      }
    });
    if (e != null) {
      return provider.fieldsInTypeOf(e);
    }
    return emptySet();
  }

  private IndexedElement referredField(AbstractCustomOption option, OptionField field,
      Provider<IndexedElement> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField current : options.fieldsOf(option)) {
      if (current == field) {
        return (isFirstField) ? provider.get() : optionFields.sourceOf(previous);
      }
      previous = current;
      isFirstField = false;
    }
    if (field == null) {
      if (previous == null) {
        return provider.get();
      }
      return optionFields.sourceOf(previous);
    }
    return null;
  }

  private class MessageFieldDescriptorProvider implements IEObjectDescriptionsProvider {
    @Override public Collection<IEObjectDescription> fieldsInTypeOf(IndexedElement e) {
      Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
      if (e instanceof MessageField) {
        Message fieldType = modelFinder.messageTypeOf((MessageField) e);
        for (MessageElement element : fieldType.getElements()) {
          IEObjectDescription d = describe(element);
          if (d != null) {
            descriptions.add(d);
          }
        }
      }
      if (e instanceof Group) {
        for (GroupElement element : ((Group) e).getElements()) {
          IEObjectDescription d = describe(element);
          if (d != null) {
            descriptions.add(d);
          }
        }
      }
      return descriptions;
    }

    private IEObjectDescription describe(EObject e) {
      if (!(e instanceof IndexedElement)) {
        return null;
      }
      String name = options.nameForOption((IndexedElement) e);
      return create(name, e);
    }
  }

  private class ExtensionFieldDescriptorProvider implements IEObjectDescriptionsProvider {
    @Override public Collection<IEObjectDescription> fieldsInTypeOf(IndexedElement e) {
      if (!(e instanceof MessageField)) {
        return emptyList();
      }
      Message fieldType = modelFinder.messageTypeOf((MessageField) e);
      if (fieldType == null) {
        return emptyList();
      }
      Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
      for (TypeExtension extension : modelFinder.localExtensionsOf(fieldType)) {
        for (MessageElement element : extension.getElements()) {
          if (!(element instanceof IndexedElement)) {
            continue;
          }
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

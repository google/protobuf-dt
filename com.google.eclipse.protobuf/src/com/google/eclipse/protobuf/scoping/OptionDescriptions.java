/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.OptionType.*;
import static com.google.eclipse.protobuf.scoping.QualifiedNames.addLeadingDot;
import static java.util.Collections.emptyList;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OptionDescriptions {

  private static final Map<Class<?>, OptionType> OPTION_TYPES_BY_CONTAINER = new HashMap<Class<?>, OptionType>();

  static {
    OPTION_TYPES_BY_CONTAINER.put(Protobuf.class, FILE);
    OPTION_TYPES_BY_CONTAINER.put(Enum.class, ENUM);
    OPTION_TYPES_BY_CONTAINER.put(Message.class, MESSAGE);
    OPTION_TYPES_BY_CONTAINER.put(Service.class, SERVICE);
    OPTION_TYPES_BY_CONTAINER.put(Rpc.class, RPC);
  }

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private IQualifiedNameProvider nameProvider;

  Collection <IEObjectDescription> builtInOptionProperties(BuiltInOption option) {
    ProtoDescriptor descriptor = descriptorProvider.get();
    Collection<Property> properties = descriptor.availableOptionPropertiesFor(option.eContainer());
    if (!properties.isEmpty()) return describe(properties);
    return emptyList();
  }

  Collection <IEObjectDescription> localCustomOptionProperties(EObject root, CustomOption option) {
    return localCustomOptionProperties(root, option, 0);
  }

  private  Collection <IEObjectDescription> localCustomOptionProperties(EObject root, CustomOption option, int level) {
    OptionType optionType = optionType(option);
    if (optionType == null) return emptyList();
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (isExtendingOptionMessage(element, optionType)) {
        ExtendMessage extend = (ExtendMessage) element;
        for (MessageElement e : extend.getElements()) {
          if (!(e instanceof Property)) continue;
          List<QualifiedName> names = localNamesProvider.namesOf(e);
          int nameCount = names.size();
          for (int i = level; i < nameCount; i++) {
            descriptions.add(create(names.get(i), element));
          }
          descriptions.addAll(fullyQualifiedNamesOf(element));
        }
        continue;
      }
      if (element instanceof Message) {
        descriptions.addAll(localCustomOptionProperties(element, option, level + 1));
      }
    }
    return descriptions;
  }

  private OptionType optionType(CustomOption option) {
    EObject container = option.eContainer();
    for (Entry<Class<?>, OptionType> optionTypeByContainer : OPTION_TYPES_BY_CONTAINER.entrySet()) {
      if (optionTypeByContainer.getKey().isInstance(container)) {
        return optionTypeByContainer.getValue();
      }
    }
    return null;
  }

  private boolean isExtendingOptionMessage(EObject o, OptionType optionType) {
    if (!(o instanceof ExtendMessage)) return false;
    Message message = messageFrom((ExtendMessage) o);
    if (message == null) return false;
    return optionType.messageName.equals(message.getName());
  }

  private Message messageFrom(ExtendMessage extend) {
    MessageRef ref = extend.getMessage();
    return ref == null ? null : ref.getType();
  }

  // TODO remove duplication in TypeDescriptions
  private Collection<IEObjectDescription> fullyQualifiedNamesOf(EObject obj) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    QualifiedName fqn = nameProvider.getFullyQualifiedName(obj);
    descriptions.add(create(fqn, obj));
    descriptions.add(create(addLeadingDot(fqn), obj));
    return descriptions;
  }

  private Collection<IEObjectDescription> describe(Collection<Property> properties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : properties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.OptionType.typeOf;
import static java.util.Collections.emptySet;

import java.util.*;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping">Xtext Scoping</a>
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider implements ScopeProvider {
  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private ComplexTypeFinderDelegate complexTypeFinderDelegate;
  @Inject private CustomOptionFieldFinder customOptionFieldFinder;
  @Inject private CustomOptionFieldNameFinder customOptionFieldNameFinder;
  @Inject private CustomOptionFinderDelegate customOptionFinderDelegate;
  @Inject private ExtensionFieldNameFinderDelegate extensionFieldNameFinderDelegate;
  @Inject private ExtensionFieldFinderDelegate extensionFieldFinderDelegate;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private MessageFieldFinderDelegate messageFieldFinderDelegate;
  @Inject private MessageFields messageFields;
  @Inject private ModelElementFinder modelElementFinder;
  @Inject private ModelObjects modelObjects;
  @Inject private NormalFieldNameFinderDelegate normalFieldNameFinderDelegate;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private NativeOptionDescriptions nativeOptionDescriptions;
  @Inject private Options options;

  @SuppressWarnings("unused")
  public IScope scope_ComplexTypeLink_target(ComplexTypeLink link, EReference r) {
    EObject c = link.eContainer();
    if (c instanceof MessageField) {
      MessageField field = (MessageField) c;
      Collection<IEObjectDescription> complexTypes = potentialComplexTypesFor(field);
      return createScope(complexTypes);
    }
    return createEmptyScope();
  }

  @Override public Collection<IEObjectDescription> potentialComplexTypesFor(MessageField field) {
    return modelElementFinder.find(field, complexTypeFinderDelegate, ComplexType.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_ExtensibleTypeLink_target(ExtensibleTypeLink link, EReference r) {
    Protobuf root = modelObjects.rootOf(link);
    Collection<IEObjectDescription> extensibleTypes = extensibleTypes(root);
    return createScope(extensibleTypes);
  }

  @Override public Collection<IEObjectDescription> potentialExtensibleTypesFor(TypeExtension extension) {
    Protobuf root = modelObjects.rootOf(extension);
    return extensibleTypes(root);
  }

  private Collection<IEObjectDescription> extensibleTypes(Protobuf root) {
    return modelElementFinder.find(root, complexTypeFinderDelegate, ExtensibleType.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_MessageLink_target(MessageLink link, EReference r) {
    Protobuf root = modelObjects.rootOf(link);
    Collection<IEObjectDescription> messages = messagesInScope(root);
    return createScope(messages);
  }

  @Override public Collection<IEObjectDescription> potentialMessagesFor(Rpc rpc) {
    Protobuf root = modelObjects.rootOf(rpc);
    return messagesInScope(root);
  }

  private Collection<IEObjectDescription> messagesInScope(Protobuf root) {
    return modelElementFinder.find(root, complexTypeFinderDelegate, Message.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_LiteralLink_target(LiteralLink link, EReference r) {
    EObject container = link.eContainer();
    Enum anEnum = null;
    if (container instanceof DefaultValueFieldOption) {
      container = container.eContainer();
    }
    if (container instanceof AbstractOption) {
      AbstractOption option = (AbstractOption) container;
      if (options.isNative(option)) {
        ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
        IndexedElement e = options.rootSourceOf(option);
        anEnum = descriptor.enumTypeOf((MessageField) e);
      }
    }
    if (container instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) container;
      container = options.sourceOf(option);
    }
    if (container instanceof SimpleValueField) {
      SimpleValueField field = (SimpleValueField) container;
      container = field.getName().getTarget();
    }
    if (container instanceof MessageField) {
      anEnum = messageFields.enumTypeOf((MessageField) container);
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }

  @SuppressWarnings("unused")
  public IScope scope_OptionSource_target(OptionSource source, EReference r) {
    EObject c = source.eContainer();
    if (c instanceof AbstractOption) {
      AbstractOption option = (AbstractOption) c;
      if (options.isNative(option)) {
        return createScope(nativeOptionDescriptions.sources(option));
      }
    }
    if (c instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) c;
      return createScope(potentialSourcesFor(option));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @Override public Collection<IEObjectDescription> potentialSourcesFor(AbstractCustomOption option) {
    OptionType optionType = typeOf((AbstractOption) option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = modelElementFinder.find(option, customOptionFinderDelegate, optionType);
    }
    return descriptions;
  }

  @SuppressWarnings("unused")
  public IScope scope_OptionField_target(OptionField field, EReference r) {
    return createScope(allPossibleSourcesOf(field));
  }

  private Collection<IEObjectDescription> allPossibleSourcesOf(OptionField field) {
    if (field == null) {
      return emptySet();
    }
    EObject container = field.eContainer();
    if (container instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) container;
      if (field instanceof MessageOptionField) {
        return customOptionFieldFinder.findOptionFields(option, messageFieldFinderDelegate, field);
      }
      return customOptionFieldFinder.findOptionFields(option, extensionFieldFinderDelegate, field);
    }
    return emptySet();
  }

  /** {@inheritDoc} */
  @Override public Collection<IEObjectDescription> potentialMessageFieldsFor(AbstractCustomOption option) {
    return customOptionFieldFinder.findOptionFields(option, messageFieldFinderDelegate);
  }

  /** {@inheritDoc} */
  @Override public Collection<IEObjectDescription> potentialExtensionFieldsFor(AbstractCustomOption option) {
    return customOptionFieldFinder.findOptionFields(option, extensionFieldFinderDelegate);
  }

  @SuppressWarnings("unused")
  public IScope scope_FieldName_target(FieldName name, EReference r) {
    return createScope(findSources(name));
  }

  private Collection<IEObjectDescription> findSources(FieldName name) {
    ComplexValue value = container(name);
    if (value == null) {
      return emptySet();
    }
    if (name instanceof NormalFieldName) {
      return potentialNormalFieldNames(value);
    }
    return potentialExtensionFieldNames(value);
  }

  private ComplexValue container(FieldName name) {
    EObject container = name;
    while (container != null) {
      if (container instanceof ComplexValue) {
        return (ComplexValue) container;
      }
      container = container.eContainer();
    }
    return null;
  }

  @Override public Collection<IEObjectDescription> potentialNormalFieldNames(ComplexValue value) {
    return customOptionFieldNameFinder.findFieldNamesSources(value, normalFieldNameFinderDelegate);
  }

  @Override public Collection<IEObjectDescription> potentialExtensionFieldNames(ComplexValue value) {
    return customOptionFieldNameFinder.findFieldNamesSources(value, extensionFieldNameFinderDelegate);
  }

  private static IScope createEmptyScope() {
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

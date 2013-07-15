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

import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.ExtensibleType;
import com.google.eclipse.protobuf.protobuf.ExtensibleTypeLink;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.GroupElement;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.LiteralLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.MessageLink;
import com.google.eclipse.protobuf.protobuf.MessageOptionField;
import com.google.eclipse.protobuf.protobuf.NormalFieldName;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.SimpleValueField;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping">Xtext Scoping</a>
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider implements ScopeProvider {
  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private ComplexTypeFinderStrategy complexTypeFinderDelegate;
  @Inject private CustomOptionFieldFinder customOptionFieldFinder;
  @Inject private CustomOptionFieldNameFinder customOptionFieldNameFinder;
  @Inject private CustomOptionFinderStrategy customOptionFinderDelegate;
  @Inject private ExtensionFieldNameFinderStrategy extensionFieldNameFinderDelegate;
  @Inject private ExtensionFieldFinderStrategy extensionFieldFinderDelegate;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private MessageFieldFinderStrategy messageFieldFinderDelegate;
  @Inject private MessageFields messageFields;
  @Inject private ModelElementFinder modelElementFinder;
  @Inject private ModelObjects modelObjects;
  @Inject private NormalFieldNameFinderStrategy normalFieldNameFinderDelegate;
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
    EObject c = link.eContainer();
    Collection<IEObjectDescription> extensibleTypes =
        modelElementFinder.find(c, complexTypeFinderDelegate, ExtensibleType.class);
    return createScope(extensibleTypes);
  }

  @Override public Collection<IEObjectDescription> potentialExtensibleTypesFor(TypeExtension extension) {
    Protobuf root = modelObjects.rootOf(extension);
    return modelElementFinder.find(root, complexTypeFinderDelegate, ExtensibleType.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_MessageLink_target(MessageLink link, EReference r) {
    Protobuf root = modelObjects.rootOf(link);
    Collection<IEObjectDescription> messages = allMessages(root);
    return createScope(messages);
  }

  @Override public Collection<IEObjectDescription> potentialMessagesFor(Rpc rpc) {
    Protobuf root = modelObjects.rootOf(rpc);
    return allMessages(root);
  }

  @Override public Collection<IEObjectDescription> potentialMessagesFor(Stream stream) {
    Protobuf root = modelObjects.rootOf(stream);
    return allMessages(root);
  }

  private Collection<IEObjectDescription> allMessages(Protobuf root) {
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

      if (c instanceof GroupElement) {
        EObject container = c.eContainer();
        if (container instanceof Group) {
          OptionType optionType = OptionType.findOptionTypeForLevelOf(container.eContainer());
          return createScope(optionType != null 
              ? modelElementFinder.find(option, customOptionFinderDelegate, optionType)
              : Collections.<IEObjectDescription>emptySet());
        }
      }

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

  @Override public Collection<IEObjectDescription> potentialMessageFieldsFor(AbstractCustomOption option) {
    return customOptionFieldFinder.findOptionFields(option, messageFieldFinderDelegate);
  }

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

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
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider implements Scoping {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private AstWalker astWalker;
  @Inject private CustomOptionFieldScopeFinder customOptionFieldScopeFinder;
  @Inject private CustomOptionScopeFinder customOptionScopeFinder;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldNotationScopeFinder fieldNotationScopeFinder;
  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder modelFinder;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private NativeOptionDescriptions nativeOptionDescriptions;
  @Inject private Options options;
  @Inject private TypeScopeFinder typeScopeFinder;

  @SuppressWarnings("unused") public IScope scope_ComplexTypeLink_target(ComplexTypeLink link, EReference r) {
    EObject c = link.eContainer();
    if (c instanceof MessageField) { return createScope(allPossibleTypesFor((MessageField) c)); }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @Override public Collection<IEObjectDescription> allPossibleTypesFor(MessageField field) {
    return astWalker.traverseAst(field, typeScopeFinder, ComplexType.class);
  }

  @SuppressWarnings("unused") public IScope scope_ExtensibleTypeLink_target(ExtensibleTypeLink link, EReference r) {
    return createScope(extensibleTypesFor(link));
  }

  @Override public Collection<IEObjectDescription> allPossibleTypesFor(TypeExtension extension) {
    return extensibleTypesFor(extension);
  }

  private Collection<IEObjectDescription> extensibleTypesFor(EObject o) {
    Protobuf root = modelFinder.rootOf(o);
    return astWalker.traverseAst(root, typeScopeFinder, ExtensibleType.class);
  }

  @SuppressWarnings("unused") public IScope scope_MessageLink_target(MessageLink link, EReference r) {
    return createScope(messagesFor(link));
  }

  @Override public Collection<IEObjectDescription> allPossibleMessagesFor(Rpc rpc) {
    return messagesFor(rpc);
  }

  private Collection<IEObjectDescription> messagesFor(EObject o) {
    Protobuf root = modelFinder.rootOf(o);
    return astWalker.traverseAst(root, typeScopeFinder, Message.class);
  }

  @SuppressWarnings("unused") public IScope scope_LiteralLink_target(LiteralLink link, EReference r) {
    EObject container = link.eContainer();
    Enum anEnum = null;
    if (container instanceof DefaultValueFieldOption) {
      container = container.eContainer();
    }
    if (container instanceof NativeOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      IndexedElement e = options.rootSourceOf((Option) container);
      anEnum = descriptor.enumTypeOf((MessageField) e);
    }
    if (container instanceof CustomOption) {
      CustomOption option = (CustomOption) container;
      container = options.sourceOf(option);
    }
    if (container instanceof NativeFieldOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      IndexedElement c = fieldOptions.rootSourceOf((FieldOption) container);
      anEnum = descriptor.enumTypeOf((MessageField) c);
    }
    if (container instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) container;
      container = fieldOptions.sourceOf(option);
    }
    if (container instanceof SimpleValueField) {
      SimpleValueField field = (SimpleValueField) container;
      container = field.getName().getTarget();
    }
    if (container instanceof MessageField) {
      anEnum = modelFinder.enumTypeOf((MessageField) container);
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }

  @SuppressWarnings("unused") public IScope scope_OptionSource_target(OptionSource source, EReference r) {
    EObject c = source.eContainer();
    if (c instanceof NativeOption) {
      NativeOption option = (NativeOption) c;
      return createScope(nativeOptionDescriptions.sources(option));
    }
    if (c instanceof NativeFieldOption) {
      NativeFieldOption option = (NativeFieldOption) c;
      return createScope(nativeOptionDescriptions.sources(option));
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      return createScope(allPossibleSourcesOf(option));
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      return createScope(allPossibleSourcesOf(option));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @Override public Collection<IEObjectDescription> allPossibleSourcesOf(CustomOption option) {
    OptionType optionType = typeOf(option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(option, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> allPossibleSourcesOf(CustomFieldOption option) {
    OptionType optionType = typeOf(option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(option, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @SuppressWarnings("unused") public IScope scope_OptionField_target(OptionField field, EReference r) {
    return createScope(allPossibleSourcesOf(field));
  }

  private Collection<IEObjectDescription> allPossibleSourcesOf(OptionField field) {
    if (field == null) {
      return emptySet();
    }
    EObject container = field.eContainer();
    if (container instanceof CustomOption) {
      CustomOption option = (CustomOption) container;
      if (field instanceof MessageOptionField) { return findSources(option, (MessageOptionField) field); }
      return findSources(option, (ExtensionOptionField) field);
    }
    if (container instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) container;
      if (field instanceof MessageOptionField) { return findSources(option, (MessageOptionField) field); }
      return findSources(option, (ExtensionOptionField) field);
    }
    return emptySet();
  }

  @Override public Collection<IEObjectDescription> allPossibleNormalFieldsOf(CustomOption option) {
    return findSources(option, (MessageOptionField) null);
  }

  @Override public Collection<IEObjectDescription> allPossibleNormalFieldsOf(CustomFieldOption option) {
    return findSources(option, (MessageOptionField) null);
  }

  @Override public Collection<IEObjectDescription> allPossibleExtensionFieldsOf(CustomOption option) {
    return findSources(option, (ExtensionOptionField) null);
  }

  @Override public Collection<IEObjectDescription> allPossibleExtensionFieldsOf(CustomFieldOption option) {
    return findSources(option, (ExtensionOptionField) null);
  }

  private Collection<IEObjectDescription> findSources(CustomOption option, MessageOptionField field) {
    return customOptionFieldScopeFinder.findScope(option, field);
  }

  private Collection<IEObjectDescription> findSources(CustomOption option, ExtensionOptionField field) {
    return customOptionFieldScopeFinder.findScope(option, field);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption option, MessageOptionField field) {
    return customOptionFieldScopeFinder.findScope(option, field);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption option, ExtensionOptionField field) {
    return customOptionFieldScopeFinder.findScope(option, field);
  }

  @SuppressWarnings("unused") public IScope scope_FieldName_target(FieldName name, EReference r) {
    return createScope(findSources(name));
  }

  private Collection<IEObjectDescription> findSources(FieldName name) {
    ComplexValue value = container(name);
    if (value == null) {
      return emptySet();
    }
    if (name instanceof NormalFieldName) { return allPossibleNamesOfNormalFieldsOf(value); }
    return allPossibleNamesOfExtensionFieldsOf(value);
  }

  private ComplexValue container(FieldName name) {
    EObject container = name;
    while (container != null) {
      if (container instanceof ComplexValue) { return (ComplexValue) container; }
      container = container.eContainer();
    }
    return null;
  }

  @Override public Collection<IEObjectDescription> allPossibleNamesOfNormalFieldsOf(ComplexValue value) {
    return fieldNotationScopeFinder.sourceOfNormalFieldNamesOf(value);
  }

  @Override public Collection<IEObjectDescription> allPossibleNamesOfExtensionFieldsOf(ComplexValue value) {
    return fieldNotationScopeFinder.sourceOfExtensionFieldNamesOf(value);
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

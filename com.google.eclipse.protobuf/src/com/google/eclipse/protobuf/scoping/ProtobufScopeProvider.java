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

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import java.util.*;

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
  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder modelFinder;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private NativeOptionDescriptions nativeOptionDescriptions;
  @Inject private Options options;
  @Inject private TypeScopeFinder typeScopeFinder;

  @SuppressWarnings("unused")
  public IScope scope_TypeRef_type(TypeRef typeRef, EReference reference) {
    EObject c = typeRef.eContainer();
    if (c instanceof Property) {
      Property property = (Property) c;
      return createScope(astWalker.traverseAst(property, typeScopeFinder, Type.class));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @SuppressWarnings("unused")
  public IScope scope_MessageRef_type(MessageRef messageRef, EReference reference) {
    Protobuf root = modelFinder.rootOf(messageRef);
    return createScope(astWalker.traverseAst(root, typeScopeFinder, Message.class));
  }

  @SuppressWarnings("unused")
  public IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject c = literalRef.eContainer();
    Enum anEnum = null;
    if (c instanceof DefaultValueFieldOption) {
      EObject optionContainer = c.eContainer();
      if (optionContainer instanceof Property) anEnum = modelFinder.enumTypeOf((Property) optionContainer);
    }
    if (c instanceof NativeOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      Field f = options.sourceOf((Option) c);
      anEnum = descriptor.enumTypeOf((Property) f);
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      c = options.lastFieldSourceFrom(option);
      if (c == null) c = options.sourceOf(option);
    }
    if (c instanceof NativeFieldOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      Field f = fieldOptions.sourceOf((FieldOption) c);
      anEnum = descriptor.enumTypeOf((Property) f);
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      c = fieldOptions.lastFieldSourceFrom(option);
      if (c == null) c = fieldOptions.sourceOf(option);
    }
    if (c instanceof Property) {
      anEnum = modelFinder.enumTypeOf((Property) c);
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }

  @SuppressWarnings("unused")
  public IScope scope_OptionSource_optionField(OptionSource optionSource, EReference reference) {
    EObject c = optionSource.eContainer();
    if (c instanceof NativeOption) {
      NativeOption option = (NativeOption) c;
      return createScope(nativeOptionDescriptions.properties(option));
    }
    if (c instanceof NativeFieldOption) {
      NativeFieldOption option = (NativeFieldOption) c;
      return createScope(nativeOptionDescriptions.properties(option));
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      return createScope(findSources(option));
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      return createScope(findSources(option));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  @Override public Collection<IEObjectDescription> findSources(CustomOption option) {
    OptionType optionType = typeOf(option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(option, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> findSources(CustomFieldOption option) {
    OptionType optionType = typeOf(option);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(option, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @SuppressWarnings("unused") 
  public IScope scope_OptionMessageFieldSource_optionMessageField(OptionMessageFieldSource source, 
      EReference reference) {
    return createScope(findSources(source));
  }
  
  @Override public Collection<IEObjectDescription> findSources(OptionMessageFieldSource source) {
    EObject container = source.eContainer();
    if (container instanceof CustomOption) {
      return findSources((CustomOption) container, source);
    }
    if (container instanceof CustomFieldOption) {
      return findSources((CustomFieldOption) container, source);
    }
    return emptySet();
  }
  
  @Override public Collection<IEObjectDescription> findNextMessageFieldSources(CustomOption option) {
    return findSources(option, (OptionMessageFieldSource) null);
  }

  @Override public Collection<IEObjectDescription> findNextMessageFieldSources(CustomFieldOption option) {
    return findSources(option, (OptionMessageFieldSource) null);
  }

  private Collection<IEObjectDescription> findSources(CustomOption option, OptionMessageFieldSource source) {
    return customOptionFieldScopeFinder.findScope(option, source);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption option, OptionMessageFieldSource source) {
    return customOptionFieldScopeFinder.findScope(option, source);
  }
  
  @SuppressWarnings("unused") 
  public IScope scope_OptionExtendMessageFieldSource_optionExtendMessageField(OptionExtendMessageFieldSource source, 
      EReference reference) {
    return createScope(findSources(source));
  }
  
  @Override public Collection<IEObjectDescription> findSources(OptionExtendMessageFieldSource source) {
    EObject container = source.eContainer();
    if (container instanceof CustomOption) {
      return findSources((CustomOption) container, source);
    }
    if (container instanceof CustomFieldOption) {
      return findSources((CustomFieldOption) container, source);
    }
    return emptySet();
  }

  @Override public Collection<IEObjectDescription> findNextExtendMessageFieldSources(CustomOption option) {
    return findSources(option, (OptionExtendMessageFieldSource) null);
  }

  @Override public Collection<IEObjectDescription> findNextExtendMessageFieldSources(CustomFieldOption option) {
    return findSources(option, (OptionExtendMessageFieldSource) null);
  }

  private Collection<IEObjectDescription> findSources(CustomOption option, OptionExtendMessageFieldSource source) {
    return customOptionFieldScopeFinder.findScope(option, source);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption option, OptionExtendMessageFieldSource source) {
    return customOptionFieldScopeFinder.findScope(option, source);
  }
  
  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

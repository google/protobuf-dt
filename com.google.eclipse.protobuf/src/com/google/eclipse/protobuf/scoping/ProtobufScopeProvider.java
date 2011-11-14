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

  @SuppressWarnings("unused")
  public IScope scope_ComplexTypeLink_target(ComplexTypeLink t, EReference r) {
    EObject c = t.eContainer();
    if (c instanceof MessageField) {
      return createScope(findTypeScope(c));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  @Override public Collection<IEObjectDescription> findTypeScope(EObject o) {
    return astWalker.traverseAst(o, typeScopeFinder, ComplexType.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_MessageLink_target(MessageLink m, EReference r) {
    return createScope(findMessageScope(m));
  }

  @Override public Collection<IEObjectDescription> findMessageScope(EObject o) {
    Protobuf root = modelFinder.rootOf(o);
    return astWalker.traverseAst(root, typeScopeFinder, Message.class);
  }

  @SuppressWarnings("unused")
  public IScope scope_LiteralLink_target(LiteralLink l, EReference r) {
    EObject container = l.eContainer();
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
    if (container instanceof MessageField) {
      anEnum = modelFinder.enumTypeOf((MessageField) container);
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }
  
  @SuppressWarnings("unused")
  public IScope scope_OptionSource_optionField(OptionSource s, EReference r) {
    EObject c = s.eContainer();
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
      return createScope(findScope(option));
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      return createScope(findScope(option));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  @Override public Collection<IEObjectDescription> findScope(CustomOption o) {
    OptionType optionType = typeOf(o);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(o, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @Override public Collection<IEObjectDescription> findScope(CustomFieldOption o) {
    OptionType optionType = typeOf(o);
    Collection<IEObjectDescription> descriptions = emptySet();
    if (optionType != null) {
      descriptions = astWalker.traverseAst(o, customOptionScopeFinder, optionType);
    }
    return descriptions;
  }

  @SuppressWarnings("unused") 
  public IScope scope_OptionMessageFieldSource_optionMessageField(OptionMessageFieldSource s, EReference r) {
    return createScope(findScope(s));
  }
  
  @Override public Collection<IEObjectDescription> findScope(OptionMessageFieldSource s) {
    EObject container = s.eContainer();
    if (container instanceof CustomOption) {
      return findSources((CustomOption) container, s);
    }
    if (container instanceof CustomFieldOption) {
      return findSources((CustomFieldOption) container, s);
    }
    return emptySet();
  }
  
  @Override public Collection<IEObjectDescription> findMessageFieldScope(CustomOption o) {
    return findSources(o, (OptionMessageFieldSource) null);
  }

  @Override public Collection<IEObjectDescription> findMessageFieldScope(CustomFieldOption o) {
    return findSources(o, (OptionMessageFieldSource) null);
  }

  private Collection<IEObjectDescription> findSources(CustomOption o, OptionMessageFieldSource s) {
    return customOptionFieldScopeFinder.findScope(o, s);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption o, OptionMessageFieldSource s) {
    return customOptionFieldScopeFinder.findScope(o, s);
  }
  
  @SuppressWarnings("unused") 
  public IScope scope_OptionExtendMessageFieldSource_optionExtendMessageField(OptionExtendMessageFieldSource s, 
      EReference r) {
    return createScope(findScope(s));
  }
  
  @Override public Collection<IEObjectDescription> findScope(OptionExtendMessageFieldSource s) {
    EObject container = s.eContainer();
    if (container instanceof CustomOption) {
      return findSources((CustomOption) container, s);
    }
    if (container instanceof CustomFieldOption) {
      return findSources((CustomFieldOption) container, s);
    }
    return emptySet();
  }

  @Override public Collection<IEObjectDescription> findExtendMessageFieldScope(CustomOption o) {
    return findSources(o, (OptionExtendMessageFieldSource) null);
  }

  @Override public Collection<IEObjectDescription> findExtendMessageFieldScope(CustomFieldOption o) {
    return findSources(o, (OptionExtendMessageFieldSource) null);
  }

  private Collection<IEObjectDescription> findSources(CustomOption option, OptionExtendMessageFieldSource s) {
    return customOptionFieldScopeFinder.findScope(option, s);
  }

  private Collection<IEObjectDescription> findSources(CustomFieldOption option, OptionExtendMessageFieldSource o) {
    return customOptionFieldScopeFinder.findScope(option, o);
  }
  
  @SuppressWarnings("unused") 
  public IScope scope_NormalFieldNotationNameSource_property(NormalFieldNotationNameSource s, EReference r) {
    return findScope(s);
  }

  @SuppressWarnings("unused") 
  public IScope scope_ExtensionFieldNotationNameSource_extension(ExtensionFieldNotationNameSource s, EReference r) {
    return findScope(s);
  }

  private IScope findScope(FieldNotationNameSource s) {
    return createScope(fieldNotationScopeFinder.sourceOf(s));
  }
  
  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

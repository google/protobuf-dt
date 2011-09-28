/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptySet;

import java.util.*;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.*;
import com.google.inject.Inject;

/**
 * Custom scoping description.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping">Xtext Scoping</a>
 */
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private CustomOptionDescriptions customOptionDescriptions;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ProtobufElementFinder finder;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private NativeOptionDescriptions nativeOptionDescriptions;
  @Inject private Options options;
  @Inject private TypeDescriptions typeDescriptions;

  @SuppressWarnings("unused")
  IScope scope_TypeRef_type(TypeRef typeRef, EReference reference) {
    EObject c = typeRef.eContainer();
    if (c instanceof Property) {
      Property property = (Property) c;
      return createScope(typeDescriptions.types(property));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @SuppressWarnings("unused")
  IScope scope_MessageRef_type(MessageRef messageRef, EReference reference) {
    Protobuf root = finder.rootOf(messageRef);
    return createScope(typeDescriptions.messages(root));
  }

  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject c = literalRef.eContainer();
    Enum anEnum = null;
    if (c instanceof NativeOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      Property p = options.propertyFrom((Option) c);
      anEnum = descriptor.enumTypeOf(p);
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      c = options.propertyFieldFrom(option);
      if (c == null) c = options.propertyFrom(option);
    }
    if (c instanceof Property) {
      anEnum = finder.enumTypeOf((Property) c);
    }
    if (c instanceof NativeFieldOption) {
      NativeFieldOption option = (NativeFieldOption) c;
      if (fieldOptions.isDefaultValueOption(option)) {
        Property property = (Property) option.eContainer();
        anEnum = finder.enumTypeOf(property);
      } else {
        ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
        anEnum = descriptor.enumTypeOf(option);
      }
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }

  @SuppressWarnings("unused")
  IScope scope_PropertyRef_property(PropertyRef propertyRef, EReference reference) {
    EObject c = propertyRef.eContainer();
    if (c instanceof NativeOption) {
      NativeOption option = (NativeOption) c;
      return createScope(nativeOptionDescriptions.properties(option));
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      return createScope(customOptionDescriptions.properties(option));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  @SuppressWarnings("unused") 
  IScope scope_SimplePropertyRef_property(SimplePropertyRef propertyRef, EReference reference) {
    EObject c = propertyRef.eContainer();
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      Property property = options.propertyFrom(option);
      if (property != null) {
        return createScope(customOptionDescriptions.fields(property));
      }
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

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
import static java.util.Collections.*;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.*;

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
public class ProtobufScopeProvider extends AbstractDeclarativeScopeProvider {

  private static final boolean DO_NOT_IGNORE_CASE = false;

  @Inject private CustomOptionSearchDelegate customOptionSearchDelegate;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ModelFinder modelFinder;
  @Inject private LiteralDescriptions literalDescriptions;
  @Inject private NativeOptionDescriptions nativeOptionDescriptions;
  @Inject private Options options;
  @Inject private ScopeFinder scopeFinder;
  @Inject private TypeSearchDelegate typeSearchDelegate;
  @Inject private QualifiedNameDescriptions qualifiedNamesDescriptions;

  @SuppressWarnings("unused")
  public IScope scope_TypeRef_type(TypeRef typeRef, EReference reference) {
    EObject c = typeRef.eContainer();
    if (c instanceof Property) {
      Property property = (Property) c;
      return createScope(scopeFinder.findScope(property, typeSearchDelegate, Type.class));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  @SuppressWarnings("unused")
  public IScope scope_MessageRef_type(MessageRef messageRef, EReference reference) {
    Protobuf root = modelFinder.rootOf(messageRef);
    return createScope(scopeFinder.findScope(root, typeSearchDelegate, Message.class));
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
      Property p = options.propertyFrom((Option) c);
      anEnum = descriptor.enumTypeOf(p);
    }
    if (c instanceof CustomOption) {
      CustomOption option = (CustomOption) c;
      c = options.fieldFrom(option);
      if (c == null) c = options.propertyFrom(option);
    }
    if (c instanceof NativeFieldOption) {
      ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
      Property p = fieldOptions.propertyFrom((FieldOption) c);
      anEnum = descriptor.enumTypeOf(p);
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      c = fieldOptions.fieldFrom(option);
      if (c == null) c = fieldOptions.propertyFrom(option);
    }
    if (c instanceof Property) {
      anEnum = modelFinder.enumTypeOf((Property) c);
    }
    return createScope(literalDescriptions.literalsOf(anEnum));
  }

  @SuppressWarnings("unused")
  public IScope scope_PropertyRef_property(PropertyRef propertyRef, EReference reference) {
    EObject c = propertyRef.eContainer();
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
      return createScope(scopeFinder.findScope(option, customOptionSearchDelegate, typeOf(option)));
    }
    if (c instanceof CustomFieldOption) {
      CustomFieldOption option = (CustomFieldOption) c;
      return createScope(scopeFinder.findScope(option, customOptionSearchDelegate, typeOf(option)));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  @SuppressWarnings("unused") 
  public IScope scope_MessagePropertyRef_messageProperty(MessagePropertyRef propertyRef, EReference reference) {
    EObject c = propertyRef.eContainer();
    Property property = null;
    if (c instanceof CustomOption) {
      final CustomOption option = (CustomOption) c;
      property = referencedProperty(propertyRef, option.getOptionFields(), new Provider<Property>() {
        @Override public Property get() {
          return options.propertyFrom(option);
        }
      });
    }
    if (c instanceof CustomFieldOption) {
      final CustomFieldOption option = (CustomFieldOption) c;
      property = referencedProperty(propertyRef, option.getOptionFields(), new Provider<Property>() {
        @Override public Property get() {
          return fieldOptions.propertyFrom(option);
        }
      });
    }
    if (property != null) {
      return createScope(fieldsInMessageContaining(property));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }
  
  private Property referencedProperty(MessagePropertyRef ref, List<OptionField> fields, Provider<Property> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField field : fields) {
      if (field == ref) return (isFirstField) ? provider.get() : propertyFrom(previous);
      previous = field;
      isFirstField = false;
    }
    return null;
  }
  
  private Collection <IEObjectDescription> fieldsInMessageContaining(Property p) {
    Message propertyType = modelFinder.messageTypeOf(p);
    if (propertyType == null) return emptyList();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (MessageElement e : propertyType.getElements()) {
      if (!(e instanceof Property)) continue;
      Property optionPropertyField = (Property) e;
      descriptions.add(create(optionPropertyField.getName(), optionPropertyField));
    }
    return descriptions;
  }

  @SuppressWarnings("unused") 
  public IScope scope_ExtendMessagePropertyRef_extendMessageProperty(ExtendMessagePropertyRef propertyRef, 
      EReference reference) {
    EObject c = propertyRef.eContainer();
    Property property = null;
    if (c instanceof CustomOption) {
      final CustomOption option = (CustomOption) c;
      property = referencedProperty(propertyRef, option.getOptionFields(), new Provider<Property>() {
        @Override public Property get() {
          return options.propertyFrom(option);
        }
      });
    }
    if (c instanceof CustomFieldOption) {
      final CustomFieldOption option = (CustomFieldOption) c;
      property = referencedProperty(propertyRef, option.getOptionFields(), new Provider<Property>() {
        @Override public Property get() {
          return fieldOptions.propertyFrom(option);
        }
      });
    }
    if (property != null) {
      return createScope(fieldsInExtendedVersionOfMessageContaining(property));
    }
    Set<IEObjectDescription> descriptions = emptySet();
    return createScope(descriptions);
  }

  private Property referencedProperty(ExtendMessagePropertyRef ref, List<OptionField> fields, Provider<Property> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField field : fields) {
      if (field == ref) return (isFirstField) ? provider.get() : propertyFrom(previous);
      previous = field;
      isFirstField = false;
    }
    return null;
  }

  private Collection <IEObjectDescription> fieldsInExtendedVersionOfMessageContaining(Property p) {
    Message propertyType = modelFinder.messageTypeOf(p);
    if (propertyType == null) return emptyList();
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    for (ExtendMessage extend : modelFinder.extensionsOf(propertyType)) {
      for (MessageElement e : extend.getElements()) {
        if (!(e instanceof Property)) continue;
        Property optionPropertyField = (Property) e;
        descriptions.addAll(qualifiedNamesDescriptions.qualifiedNames(optionPropertyField));
        descriptions.add(create(optionPropertyField.getName(), optionPropertyField));
      }
    }
    return descriptions;
  }

  private Property propertyFrom(OptionField field) {
    if (field instanceof MessagePropertyRef) {
      MessagePropertyRef ref = (MessagePropertyRef) field;
      return ref.getMessageProperty();
    }
    if (field instanceof ExtendMessagePropertyRef) {
      ExtendMessagePropertyRef ref = (ExtendMessagePropertyRef) field;
      return ref.getExtendMessageProperty();
    }
    return null;
  }
  
  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

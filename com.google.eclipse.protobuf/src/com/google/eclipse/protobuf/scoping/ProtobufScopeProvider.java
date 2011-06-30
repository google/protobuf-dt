/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.QualifiedNames.addLeadingDot;
import static java.util.Collections.emptyList;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
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

  @Inject private FieldOptions fieldOptions;
  @Inject private ProtobufElementFinder finder;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private IQualifiedNameProvider nameProvider;
  @Inject private ImportUriResolver uriResolver;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private ImportedNamesProvider importedNamesProvider;
  @Inject private PackageResolver packageResolver;

  @SuppressWarnings("unused")
  IScope scope_TypeReference_type(TypeReference typeRef, EReference reference) {
    Protobuf root = finder.rootOf(typeRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    EObject current = typeRef.eContainer().eContainer(); // get message of the property containing the TypeReference
    while (current != null) {
      descriptions.addAll(typesIn(current));
      current = current.eContainer();
    }
    descriptions.addAll(importedTypes(root, Type.class));
    return createScope(descriptions);
  }

  private Collection<IEObjectDescription> typesIn(EObject root) {
    return children(root, Type.class);
  }

  @SuppressWarnings("unused")
  IScope scope_MessageReference_type(MessageReference msgRef, EReference reference) {
    Protobuf root = finder.rootOf(msgRef);
    Set<IEObjectDescription> descriptions = new HashSet<IEObjectDescription>();
    descriptions.addAll(messagesIn(root));
    descriptions.addAll(importedTypes(root, Message.class));
    return createScope(descriptions);
  }

  private Collection<IEObjectDescription> messagesIn(Protobuf root) {
    return children(root, Message.class);
  }

  private <T extends Type> Collection<IEObjectDescription> children(EObject root, Class<T> targetType) {
    return children(root, targetType, 0);
  }

  private <T extends Type> Collection<IEObjectDescription> children(EObject root, Class<T> targetType, int level) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (EObject element : root.eContents()) {
      if (!targetType.isInstance(element)) continue;
      List<QualifiedName> names = localNamesProvider.namesOf(element);
      int nameCount = names.size();
      for (int i = level; i < nameCount; i++) {
        descriptions.add(create(names.get(i), element));
      }
      descriptions.addAll(fullyQualifiedNamesOf(element));
      // TODO investigate if groups can have messages, and if so, add those messages to the scope.
      if (element instanceof Message) {
        descriptions.addAll(children(element, targetType, level + 1));
      }
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> importedTypes(Protobuf root, Class<T> targetType) {
    List<Import> imports = finder.importsIn(root);
    if (imports.isEmpty()) return emptyList();
    Package importRootPackage = finder.packageOf(root);
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : imports) {
      Resource imported = importedResource(anImport);
      EObject importedRoot = rootElementOf(imported);
      if (importedRoot != null && arePackagesRelated(importRootPackage, importedRoot)) {
        descriptions.addAll(typesIn(importedRoot));
        continue;
      }
      descriptions.addAll(children(imported, targetType));
    }
    return descriptions;
  }

  private Resource importedResource(Import anImport) {
    ResourceSet resourceSet = finder.rootOf(anImport).eResource().getResourceSet();
    URI importUri = createURI(uriResolver.apply(anImport));
    try {
      return resourceSet.getResource(importUri, true);
    } catch (Throwable t) {
      return null;
    }
  }

  private /* Protobuf */ EObject rootElementOf(Resource resource) {
    if (resource instanceof XtextResource) return ((XtextResource) resource).getParseResult().getRootASTElement();
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof EObject) return (EObject) next;
    }
    return null;
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = finder.packageOf(root);
    return packageResolver.areRelated(aPackage, p);
  }

  private <T extends Type> Collection<IEObjectDescription> children(Resource resource, Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      if (!targetType.isInstance(next)) continue;
      T type = targetType.cast(next);
      descriptions.addAll(fullyQualifiedNamesOf(type));
      for (QualifiedName name : importedNamesProvider.namesOf(type)) {
        descriptions.add(create(name, type));
      }
    }
    return descriptions;
  }

  private Collection<IEObjectDescription> fullyQualifiedNamesOf(EObject obj) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    QualifiedName fqn = nameProvider.getFullyQualifiedName(obj);
    descriptions.add(create(fqn, obj));
    descriptions.add(create(addLeadingDot(fqn), obj));
    return descriptions;
  }

  @SuppressWarnings("unused")
  IScope scope_LiteralRef_literal(LiteralRef literalRef, EReference reference) {
    EObject container = literalRef.eContainer();
    if (container instanceof Property) {
      Enum anEnum = finder.enumTypeOf((Property) container);
      if (anEnum != null) return scopeForLiterals(anEnum);
    }
    Enum anEnum = enumTypeOfOption(container);
    if (anEnum != null) return scopeForLiterals(anEnum);
    return null;
  }

  private Enum enumTypeOfOption(EObject mayBeOption) {
    ProtoDescriptor descriptor = descriptorProvider.get();
    if (mayBeOption instanceof Option) return descriptor.enumTypeOf((Option) mayBeOption);
    if (mayBeOption instanceof FieldOption) {
      FieldOption option = (FieldOption) mayBeOption;
      if (fieldOptions.isDefaultValueOption(option)) {
        Property property = (Property) option.eContainer();
        return finder.enumTypeOf(property);
      }
      return descriptor.enumTypeOf(option);
    }
    return null;
  }

  private static IScope scopeForLiterals(Enum anEnum) {
    Collection<IEObjectDescription> descriptions = describeLiterals(anEnum);
    return createScope(descriptions);
  }

  private static Collection<IEObjectDescription> describeLiterals(Enum anEnum) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Literal literal : getAllContentsOfType(anEnum, Literal.class))
      descriptions.add(create(literal.getName(), literal));
    return descriptions;
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }


}

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

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private FieldOptions fieldOptions;
  @Inject private ProtobufElementFinder finder;
  @Inject private ImportedNamesProvider importedNamesProvider;
  @Inject private LocalNamesProvider localNamesProvider;
  @Inject private IQualifiedNameProvider nameProvider;
  @Inject private Options options;
  @Inject private PackageResolver packageResolver;
  @Inject private ImportUriResolver uriResolver;

  @SuppressWarnings("unused")
  IScope scope_TypeRef_type(TypeRef typeRef, EReference reference) {
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
  IScope scope_MessageRef_type(MessageRef messageRef, EReference reference) {
    Protobuf root = finder.rootOf(messageRef);
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
    List<Import> allImports = finder.importsIn(root);
    if (allImports.isEmpty()) return emptyList();
    return importedTypes(allImports, finder.packageOf(root), targetType);
  }

  private <T extends Type> Collection<IEObjectDescription> importedTypes(List<Import> allImports, Package aPackage,
      Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Import anImport : allImports) {
      if (isImportingDescriptor(anImport)) {
        descriptions.addAll(allBuiltInTypes(targetType));
        continue;
      }
      Resource importedResource = importedResourceFrom(anImport);
      Protobuf importedRoot = rootElementOf(importedResource);
      if (importedRoot != null) {
        descriptions.addAll(publicImportedTypes(importedRoot, targetType));
        if (arePackagesRelated(aPackage, importedRoot)) {
          descriptions.addAll(typesIn(importedRoot));
          continue;
        }
      }
      descriptions.addAll(children(importedResource, targetType));
    }
    return descriptions;
  }

  private boolean isImportingDescriptor(Import anImport) {
    String descriptorLocation = descriptorProvider.descriptorLocation().toString();
    return descriptorLocation.equals(anImport.getImportURI());
  }

  private <T extends Type> Collection<IEObjectDescription> allBuiltInTypes(Class<T> targetType) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    ProtoDescriptor descriptor = descriptorProvider.get();
    for (Type t : descriptor.allTypes()) {
      if (!targetType.isInstance(t)) continue;
      T type = targetType.cast(t);
      descriptions.addAll(fullyQualifiedNamesOf(type));
    }
    return descriptions;
  }

  private <T extends Type> Collection<IEObjectDescription> publicImportedTypes(Protobuf root, Class<T> targetType) {
    List<Import> allImports = finder.publicImportsIn(root);
    if (allImports.isEmpty()) return emptyList();
    return importedTypes(allImports, finder.packageOf(root), targetType);
  }

  private Resource importedResourceFrom(Import anImport) {
    ResourceSet resourceSet = finder.rootOf(anImport).eResource().getResourceSet();
    URI importUri = createURI(uriResolver.apply(anImport));
    try {
      return resourceSet.getResource(importUri, true);
    } catch (Throwable t) {
      return null;
    }
  }

  private Protobuf rootElementOf(Resource resource) {
    if (resource instanceof XtextResource) {
      EObject root = ((XtextResource) resource).getParseResult().getRootASTElement();
      return (Protobuf) root;
    }
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof Protobuf) return (Protobuf) next;
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
    if (mayBeOption instanceof BuiltInOption) {
      Property property = options.propertyFrom((BuiltInOption) mayBeOption);
      return descriptor.enumTypeOf(property);
    }
    if (mayBeOption instanceof BuiltInFieldOption) {
      BuiltInFieldOption option = (BuiltInFieldOption) mayBeOption;
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

  @SuppressWarnings("unused")
  IScope scope_PropertyRef_property(PropertyRef propertyRef, EReference reference) {
    EObject mayBeOption = propertyRef.eContainer();
    if (mayBeOption instanceof BuiltInOption) {
      ProtoDescriptor descriptor = descriptorProvider.get();
      EObject optionContainer = mayBeOption.eContainer();
      Collection<Property> propertyOptions = descriptor.availableOptionsFor(optionContainer);
      if (!propertyOptions.isEmpty()) return createScope(describe(propertyOptions));
    }
    List<IEObjectDescription> descriptions = Collections.emptyList();
    // return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
    return null;
  }

  private Collection<IEObjectDescription> describe(Collection<Property> properties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : properties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }

  private static IScope createScope(Iterable<IEObjectDescription> descriptions) {
    return new SimpleScope(descriptions, DO_NOT_IGNORE_CASE);
  }
}

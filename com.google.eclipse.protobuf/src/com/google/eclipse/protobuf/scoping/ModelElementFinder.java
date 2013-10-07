/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.model.util.Packages;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.resource.ResourceSets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.util.IResourceScopeCache;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class ModelElementFinder {
  @Inject private Imports imports;
  @Inject private ModelObjects modelObjects;
  @Inject private Packages packages;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;
  @Inject private ResourceSets resourceSets;
  
  @Inject private IResourceScopeCache cache;

  private class GetLocal<T> implements Provider<Collection<IEObjectDescription>> {
    private final EObject start;
    private final FinderStrategy<T> strategy;
    private final T criteria;
    
    public GetLocal(EObject start, FinderStrategy<T> strategy, T criteria) {
      this.start = start;
      this.strategy = strategy;
      this.criteria = criteria;
    }

    @Override
    public Collection<IEObjectDescription> get() {
      UniqueDescriptions descriptions = new UniqueDescriptions();
      EObject current = start.eContainer();
      while (current != null) {
        descriptions.addAll(local(current, strategy, criteria, 0));
        current = current.eContainer();
      }
      return descriptions.values();
    }
  }
  
  private class GetImports<T> implements Provider<Set<IEObjectDescription>> {
    private final Package fromImporter;
    private final Resource imported;
    private final FinderStrategy<T> strategy;
    private final T criteria;
    
    public GetImports(Package fromImporter, Resource imported, FinderStrategy<T> strategy, T criteria) {
      this.fromImporter = fromImporter;
      this.imported = imported;
      this.strategy = strategy;
      this.criteria = criteria;
    }

    @Override
    public Set<IEObjectDescription> get() {
      Set<IEObjectDescription> descriptions = newHashSet();

      Protobuf rootOfImported = resources.rootOf(imported);
      if (!protobufs.isProto2(rootOfImported)) {
        return descriptions;
      }
      if (rootOfImported != null) {
        descriptions.addAll(publicImported(rootOfImported, strategy, criteria));
        if (arePackagesRelated(fromImporter, rootOfImported)) {
          descriptions.addAll(local(rootOfImported, strategy, criteria, 0));
          return descriptions;
        }
        Package packageOfImported = modelObjects.packageOf(rootOfImported);
        descriptions.addAll(imported(fromImporter, packageOfImported, imported, strategy, criteria));
      }
      
      return descriptions;
    }
  }
  
  <T> Collection<IEObjectDescription> find(EObject start, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(local(start, strategy, criteria));
    Protobuf root = modelObjects.rootOf(start);
    descriptions.addAll(imported(root, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private <T> Collection<IEObjectDescription> local(EObject start, FinderStrategy<T> strategy, T criteria) {
    return cache.get(start, start.eResource(), new GetLocal<T>(start, strategy, criteria));
  }

  <T> Collection<IEObjectDescription> find(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    descriptions.addAll(local(start, strategy, criteria, 0));
    descriptions.addAll(imported(start, strategy, criteria));
    return unmodifiableSet(descriptions);
  }

  private <T> Collection<IEObjectDescription> local(EObject start, FinderStrategy<T> strategy, T criteria, int level) {
    UniqueDescriptions descriptions = new UniqueDescriptions();
    for (EObject element : start.eContents()) {
      descriptions.addAll(strategy.local(element, criteria, level));
      if (element instanceof Message || element instanceof Group) {
        descriptions.addAll(local(element, strategy, criteria, level + 1));
      }
    }
    return descriptions.values();
  }

  private <T> Collection<IEObjectDescription> imported(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    List<Import> allImports = protobufs.importsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private <T> Collection<IEObjectDescription> imported(List<Import> allImports, Package fromImporter,
      ResourceSet resourceSet, FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    for (Import anImport : allImports) {
      if (imports.isImportingDescriptor(anImport)) {
        descriptions.addAll(strategy.inDescriptor(anImport, criteria));
        continue;
      }
      URI resolvedUri = imports.resolvedUriOf(anImport);
      if (resolvedUri == null) {
        continue;
      }
      Resource imported = resourceSets.findResource(resourceSet, resolvedUri);
      if (imported == null) {
        continue;
      }

      Set<IEObjectDescription> cached = cache.get(criteria, imported, new GetImports<T>(fromImporter, imported, strategy, criteria));
      descriptions.addAll(cached);
    }
    return descriptions;
  }

  private <T> Collection<IEObjectDescription> publicImported(Protobuf start, FinderStrategy<T> strategy, T criteria) {
    if (!protobufs.isProto2(start)) {
      return emptySet();
    }
    List<Import> allImports = protobufs.publicImportsIn(start);
    if (allImports.isEmpty()) {
      return emptyList();
    }
    ResourceSet resourceSet = start.eResource().getResourceSet();
    return imported(allImports, modelObjects.packageOf(start), resourceSet, strategy, criteria);
  }

  private boolean arePackagesRelated(Package aPackage, EObject root) {
    Package p = modelObjects.packageOf(root);
    return packages.areRelated(aPackage, p);
  }

  private <T> Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Resource resource,
      FinderStrategy<T> strategy, T criteria) {
    Set<IEObjectDescription> descriptions = newHashSet();
    TreeIterator<Object> contents = getAllContents(resource, true);
    while (contents.hasNext()) {
      Object next = contents.next();
      descriptions.addAll(strategy.imported(fromImporter, fromImported, next, criteria));
    }
    return descriptions;
  }

  static interface FinderStrategy<T> {
    Collection<IEObjectDescription> imported(Package fromImporter, Package fromImported, Object target, T criteria);

    Collection<IEObjectDescription> inDescriptor(Import anImport, T criteria);

    Collection<IEObjectDescription> local(Object target, T criteria, int level);
  }
}

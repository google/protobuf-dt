/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static java.lang.String.format;

import static org.eclipse.xtext.util.Tuples.pair;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static com.google.eclipse.protobuf.validation.Messages.importNotFound;
import static com.google.eclipse.protobuf.validation.Messages.importingNonProto2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;

/**
 * Verifies that "imports" contain correct values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator extends AbstractDeclarativeValidator {
  @Inject private Imports imports;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;
  @Inject private ImportUriResolver uriResolver;

  @Override public void register(EValidatorRegistrar registrar) {}

  /**
   * Verifies that {@code Import}s in the given root only refer to "proto2" files. If non-proto2 {@code Import}s are
   * found, this validator will create warning markers for such {@code Import}s.
   * @param root the root containing the imports to check.
   */
  @Check public void checkNonProto2Imports(Protobuf root) {
    if (!protobufs.isProto2(root)) {
      return;
    }
    Set<Protobuf> currentlyChecking = newHashSet(root);
    HashMap<Protobuf, IsProto2> alreadyChecked = newHashMap();
    hasNonProto2Imports(root, currentlyChecking, alreadyChecked);
  }

  private boolean hasNonProto2Imports(Protobuf root, Set<Protobuf> currentlyChecking,
      Map<Protobuf, IsProto2> alreadyChecked) {
    IsProto2 isProto2 = alreadyChecked.get(root);
    if (isProto2 != null) {
      return isProto2 == IsProto2.NO;
    }
    currentlyChecking.add(root);
    Set<Pair<Import, Protobuf>> importsToCheck = newHashSet();
    boolean hasNonProto2Imports = false;
    for (Import anImport : protobufs.importsIn(root)) {
      Resource imported = imports.importedResource(anImport);
      if (imported == null) {
        continue;
      }
      Protobuf importedRoot = resources.rootOf(imported);
      isProto2 = alreadyChecked.get(importedRoot);
      if (isProto2 != null) {
        // resource was already checked.
        if (isProto2 == IsProto2.NO) {
          hasNonProto2Imports = true;
          warnNonProto2ImportFoundIn(anImport);
        }
        continue;
      }
      if (!protobufs.isProto2(importedRoot)) {
        alreadyChecked.put(importedRoot, IsProto2.NO);
        hasNonProto2Imports = true;
        warnNonProto2ImportFoundIn(anImport);
        continue;
      }
      // we have a circular dependency
      if (currentlyChecking.contains(importedRoot)) {
        continue;
      }
      // this is a proto2 file. Need to check its imports.
      importsToCheck.add(pair(anImport, importedRoot));
    }
    for (Pair<Import, Protobuf> importToCheck : importsToCheck) {
      if (hasNonProto2Imports(importToCheck.getSecond(), currentlyChecking, alreadyChecked)) {
        hasNonProto2Imports = true;
        warnNonProto2ImportFoundIn(importToCheck.getFirst());
      }
    }
    isProto2 = hasNonProto2Imports ? IsProto2.NO : IsProto2.YES;
    alreadyChecked.put(root, isProto2);
    currentlyChecking.remove(root);
    return hasNonProto2Imports;
  }

  private void warnNonProto2ImportFoundIn(Import anImport) {
    warning(importingNonProto2, anImport, IMPORT__IMPORT_URI, INSIGNIFICANT_INDEX);
  }

  /**
   * Verifies that the URI of the given {@code Import} has been resolved. If the URI has not been resolved, this
   * validator will create an error marker for the given {@code Import}.
   * @param anImport the given {@code Import}.
   */
  @Check public void checkUriIsResolved(Import anImport) {
    if (imports.isResolved(anImport)) {
      return;
    }
    uriResolver.apply(anImport);
    if (!imports.isResolved(anImport)) {
      error(format(importNotFound, anImport.getImportURI()), IMPORT__IMPORT_URI);
    }
  }

  private static enum IsProto2 {
    YES, NO;
  }
}

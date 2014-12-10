/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__PATH;
import static com.google.eclipse.protobuf.validation.Messages.importNotFound;
import static com.google.eclipse.protobuf.validation.Messages.importingUnsupportedSyntax;
import static java.lang.String.format;
import static org.eclipse.xtext.util.Tuples.pair;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Verifies that "imports" contain correct values.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator extends AbstractDeclarativeValidator {
  @Inject private Imports imports;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;

  @Override public void register(EValidatorRegistrar registrar) {}

  /**
   * Verifies that {@code Import}s in the given root only refer to files with a supported syntax. If
   * unsupported {@code Import}s are found, this validator will create warning markers for such
   * {@code Import}s.
   *
   * @param root the root containing the imports to check.
   */
  @Check public void checkUnknownSyntaxImports(Protobuf root) {
    if (!protobufs.hasKnownSyntax(root)) {
      return;
    }
    Set<Protobuf> currentlyChecking = newHashSet(root);
    HashMap<Protobuf, HasKnownSyntax> alreadyChecked = newHashMap();
    hasUnknownSyntaxImports(root, currentlyChecking, alreadyChecked);
  }

  private boolean hasUnknownSyntaxImports(Protobuf root, Set<Protobuf> currentlyChecking,
      Map<Protobuf, HasKnownSyntax> alreadyChecked) {
    HasKnownSyntax hasKnownSyntax = alreadyChecked.get(root);
    if (hasKnownSyntax != null) {
      return hasKnownSyntax == HasKnownSyntax.NO;
    }
    currentlyChecking.add(root);
    Set<Pair<Import, Protobuf>> importsToCheck = newHashSet();
    boolean hasUnsupportedImports = false;
    for (Import anImport : protobufs.importsIn(root)) {
      Resource imported = imports.importedResource(anImport);
      if (imported == null) {
        continue;
      }
      Protobuf importedRoot = resources.rootOf(imported);
      hasKnownSyntax = alreadyChecked.get(importedRoot);
      if (hasKnownSyntax != null) {
        // resource was already checked.
        if (hasKnownSyntax == HasKnownSyntax.NO) {
          hasUnsupportedImports = true;
          warnUnsupportedImportFoundIn(anImport);
        }
        continue;
      }
      if (!protobufs.hasKnownSyntax(importedRoot)) {
        alreadyChecked.put(importedRoot, HasKnownSyntax.NO);
        hasUnsupportedImports = true;
        warnUnsupportedImportFoundIn(anImport);
        continue;
      }
      // we have a circular dependency
      if (currentlyChecking.contains(importedRoot)) {
        continue;
      }
      // this is a supported file. Need to check its imports.
      importsToCheck.add(pair(anImport, importedRoot));
    }
    for (Pair<Import, Protobuf> importToCheck : importsToCheck) {
      if (hasUnknownSyntaxImports(importToCheck.getSecond(), currentlyChecking, alreadyChecked)) {
        hasUnsupportedImports = true;
        warnUnsupportedImportFoundIn(importToCheck.getFirst());
      }
    }
    hasKnownSyntax = hasUnsupportedImports ? HasKnownSyntax.NO : HasKnownSyntax.YES;
    alreadyChecked.put(root, hasKnownSyntax);
    currentlyChecking.remove(root);
    return hasUnsupportedImports;
  }

  private void warnUnsupportedImportFoundIn(Import anImport) {
    warning(importingUnsupportedSyntax, anImport, IMPORT__PATH, INSIGNIFICANT_INDEX);
  }

  /**
   * Verifies that the URI of the given {@code Import} has been resolved. If the URI has not been
   * resolved, this validator will create an error marker for the given {@code Import}.
   *
   * @param anImport the given {@code Import}.
   */
  @Check
  public void checkUriIsResolved(Import anImport) {
    if (imports.isResolved(anImport)) {
      return;
    }
    error(format(importNotFound, imports.getPath(anImport)), IMPORT__PATH);
  }

  private static enum HasKnownSyntax {
    YES, NO;
  }
}

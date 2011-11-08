/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static com.google.eclipse.protobuf.validation.Messages.importingNonProto2;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.parser.NonProto2;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.validation.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator extends AbstractDeclarativeValidator {

  @Inject private ModelFinder finder;
  @Inject private Resources resources;

  @Override public void register(EValidatorRegistrar registrar) {}

  @Check
  public void checkNonProto2Imports(Protobuf root) {
    warnIfNonProto2ImportsFound(root.eResource());
  }

  private void warnIfNonProto2ImportsFound(Resource resource) {
    Protobuf root = finder.rootOf(resource);
    if (isNotProto2(root)) return;
    ResourceSet resourceSet = resource.getResourceSet();
    for (Import anImport : finder.importsIn(root)) {
      Resource imported = resources.importedResource(anImport, resourceSet);
      if (isNotProto2(finder.rootOf(imported))) {
        acceptWarning(importingNonProto2, anImport, IMPORT__IMPORT_URI, INSIGNIFICANT_INDEX, null);
        continue;
      }
      for (Diagnostic d : imported.getWarnings()) {
        if (importingNonProto2.equals(d.getMessage())) {
          acceptWarning(importingNonProto2, anImport, IMPORT__IMPORT_URI, INSIGNIFICANT_INDEX, null);
        }
      }
    }
  }
  
  private boolean isNotProto2(Protobuf root) {
    return root instanceof NonProto2;
  }
}

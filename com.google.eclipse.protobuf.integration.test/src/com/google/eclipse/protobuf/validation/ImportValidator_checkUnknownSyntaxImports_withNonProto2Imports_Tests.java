/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static com.google.eclipse.protobuf.validation.Messages.importingUnsupportedSyntax;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Tests for <code>{@link ImportValidator#checkUnknownSyntaxImports(Protobuf)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator_checkUnknownSyntaxImports_withNonProto2Imports_Tests {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule(), new TestModule());

  @Inject private ProtobufsStub protobufs;
  @Inject private ImportValidator validator;

  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // // Create file C.proto
  //
  // syntax = 'proto2';

  // // Create file B.proto
  //
  // syntax = 'proto2';

  // syntax = "proto2";
  //
  // import "B.proto";
  // import "C.proto";
  @Test public void should_add_warning_if_import_refers_directly_to_unknown_syntax() {
    protobufs.unknownSyntaxFileName = "B.proto";
    validator.checkUnknownSyntaxImports(xtext.root());
    Import importWithUnknownSyntaxFile = findImportReferreringToFile(protobufs.unknownSyntaxFileName);
    verifyThatImportingUnknownSyntaxFileCreatedWarning(importWithUnknownSyntaxFile);
  }

  // // Create file C.proto
  //
  // syntax = 'proto2';

  // // Create file B.proto
  //
  // syntax = 'proto2';
  //
  // import "C.proto";

  // syntax = "proto2";
  //
  // import "B.proto";
  @Test public void should_add_warning_if_import_refers_indirectly_to_unknown_syntax() {
    protobufs.unknownSyntaxFileName = "C.proto";
    validator.checkUnknownSyntaxImports(xtext.root());
    Import importWithUnknownSyntaxFile = findImportReferreringToFile("B.proto");
    verifyThatImportingUnknownSyntaxFileCreatedWarning(importWithUnknownSyntaxFile);
  }

  private Import findImportReferreringToFile(String fileName) {
    List<Import> imports = protobufs.importsIn(xtext.root());
    for (Import anImport : imports) {
      if (anImport.getImportURI().endsWith(fileName)) {
        return anImport;
      }
    }
    return null;
  }

  private void verifyThatImportingUnknownSyntaxFileCreatedWarning(Import anImport) {
    verify(messageAcceptor).acceptWarning(importingUnsupportedSyntax, anImport, IMPORT__IMPORT_URI, INSIGNIFICANT_INDEX, null,
        new String[0]);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      binder().bind(Protobufs.class).to(ProtobufsStub.class);
    }
  }

  @Singleton private static class ProtobufsStub extends Protobufs {
    String unknownSyntaxFileName;

    @Override public boolean hasKnownSyntax(Protobuf protobuf) {
      URI resourceUri = protobuf.eResource().getURI();
      if (resourceUri.toString().endsWith(unknownSyntaxFileName)) {
        return false;
      }
      return super.hasKnownSyntax(protobuf);
    }
  }
}

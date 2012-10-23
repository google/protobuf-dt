/*
 * Copyright (c) 2012 Google Inc.
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
import static com.google.eclipse.protobuf.validation.Messages.importingNonProto2;

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
 * Tests for <code>{@link ImportValidator#checkNonProto2Imports(Protobuf)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportValidator_checkNonProto2Imports_withNonProto2Imports_Tests {
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
  @Test public void should_add_warning_if_import_if_import_refers_directly_to_non_proto2() {
    protobufs.nonProto2FileName = "B.proto";
    validator.checkNonProto2Imports(xtext.root());
    Import importWithNonProto2File = findImportReferreringToFile(protobufs.nonProto2FileName);
    verifyThatImportingNonProto2FileCreatedWarning(importWithNonProto2File);
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
  @Test public void should_add_warning_if_import_if_import_refers_indirectly_to_non_proto2() {
    protobufs.nonProto2FileName = "C.proto";
    validator.checkNonProto2Imports(xtext.root());
    Import importWithNonProto2File = findImportReferreringToFile("B.proto");
    verifyThatImportingNonProto2FileCreatedWarning(importWithNonProto2File);
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

  private void verifyThatImportingNonProto2FileCreatedWarning(Import anImport) {
    verify(messageAcceptor).acceptWarning(importingNonProto2, anImport, IMPORT__IMPORT_URI, INSIGNIFICANT_INDEX, null,
        new String[0]);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      binder().bind(Protobufs.class).to(ProtobufsStub.class);
    }
  }

  @Singleton private static class ProtobufsStub extends Protobufs {
    String nonProto2FileName;

    @Override public boolean isProto2(Protobuf protobuf) {
      URI resourceUri = protobuf.eResource().getURI();
      if (resourceUri.toString().endsWith(nonProto2FileName)) {
        return false;
      }
      return super.isProto2(protobuf);
    }
  }
}

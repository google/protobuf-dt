/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IImportResolver;
import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import java.util.List;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class ProtobufValidation {
  @Inject private IImportResolver resolver;

  public void validate(IEditorPart editor) {
    if (!(editor instanceof XtextEditor) || !(editor.getEditorInput() instanceof FileEditorInput)) {
      return;
    }
    XtextEditor xtextEditor = (XtextEditor) editor;
    if (!ProtobufEditorPlugIn.protobufLanguageName().equals(xtextEditor.getLanguageName())) {
      return;
    }
    validate(xtextEditor);
  }

  private void validate(XtextEditor editor) {
    final IXtextDocument document = editor.getDocument();
    if (!(document instanceof XtextDocument)) {
      return;
    }
    document.readOnly(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) {
        EObject root = rootOf(resource);
        if (root == null) {
          return;
        }
        invalidateCacheForImports(root);
        resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
        ((XtextDocument) document).checkAndUpdateAnnotations();
      }
    });
  }

  private void invalidateCacheForImports(EObject root) {
    List<Import> imports = EcoreUtil2.getAllContentsOfType(root, Import.class);
    for (Import anImport : imports) {
      resolver.invalidateCacheFor(anImport);
    }
  }

  private EObject rootOf(XtextResource resource) {
    if (resource == null) {
      return null;
    }
    IParseResult parseResult = resource.getParseResult();
    return parseResult == null ? null : parseResult.getRootASTElement();
  }
}

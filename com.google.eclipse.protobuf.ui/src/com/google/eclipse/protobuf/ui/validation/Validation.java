/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static com.google.eclipse.protobuf.ui.Internals.*;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.conversion.STRINGValueConverter;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.util.ModelNodes;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class Validation {

  static void validate(IEditorPart editor) {
    XtextEditor protoEditor = asProtoEditor(editor);
    if (protoEditor == null) return;
    validate(protoEditor);
  }

  private static XtextEditor asProtoEditor(IEditorPart editor) {
    XtextEditor xtextEditor = asXtextEditor(editor);
    if (xtextEditor == null) return null;
    if (!languageName().equals(xtextEditor.getLanguageName())) return null;
    return xtextEditor;
  }

  private static XtextEditor asXtextEditor(IEditorPart editor) {
    if (!isXtextEditorContainingWorkspaceFile(editor)) return null;
    return (XtextEditor) editor;
  }

  private static boolean isXtextEditorContainingWorkspaceFile(IEditorPart editor) {
    return editor instanceof XtextEditor && editor.getEditorInput() instanceof FileEditorInput;
  }

  private static void validate(XtextEditor editor) {
    final IXtextDocument document = editor.getDocument();
    if (!(document instanceof XtextDocument)) return;
    document.readOnly(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) {
        EObject root = rootOf(resource);
        if (root == null) return;
        resetImports(root);
        resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
        ((XtextDocument) document).checkAndUpdateAnnotations();
      }
    });
  }

  private static EObject rootOf(XtextResource resource) {
    if (resource == null) return null;
    return resource.getParseResult().getRootASTElement();
  }

  private static void resetImports(EObject root) {
    List<Import> imports = getAllContentsOfType(root, Import.class);
    for (Import anImport : imports) resetUri(anImport);
  }

  private static void resetUri(Import anImport) {
    String uri = uriAsEnteredInEditor(anImport);
    if (uri == null) return;
    anImport.setImportURI(uri);
  }

  private static String uriAsEnteredInEditor(Import anImport) {
    INode node = nodes().firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) return null;
    String text = node.getText();
    if (text == null) return null;
    STRINGValueConverter converter = injector().getInstance(STRINGValueConverter.class);
    return converter.toValue(text, node);
  }

  private static ModelNodes nodes() {
    return injector().getInstance(ModelNodes.class);
  }

  private Validation() {}
}

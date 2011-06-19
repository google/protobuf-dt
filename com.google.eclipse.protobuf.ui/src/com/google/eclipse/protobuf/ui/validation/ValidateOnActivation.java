/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.eclipse.protobuf.ui.preferences.pages.general.*;
import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.Injector;

/**
 * Validates a .proto file when it is opened or activated.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ValidateOnActivation implements IPartListener2 {

  private static final String LANGUAGE_NAME = "com.google.eclipse.protobuf.Protobuf";

  private final ModelNodes nodes = new ModelNodes();

  public void partActivated(IWorkbenchPartReference partRef) {
    final XtextEditor editor = protoEditorFrom(partRef);
    if (editor == null) return;
    if (!shouldValidateEditor(editor.getResource().getProject())) return;
    validate(editor);
  }

  private boolean shouldValidateEditor(IProject project) {
    Injector injector = ProtobufActivator.getInstance().getInjector(LANGUAGE_NAME);
    GeneralPreferencesFactory factory = injector.getInstance(GeneralPreferencesFactory.class);
    if (factory == null) return false;
    GeneralPreferences preferences = factory.preferences(project);
    return preferences.validateFilesOnActivation();
  }

  private XtextEditor protoEditorFrom(IWorkbenchPartReference partRef) {
    XtextEditor editor = xtextEditorFrom(partRef);
    if (editor == null) return null;
    if (!LANGUAGE_NAME.equals(editor.getLanguageName())) return null;
    if (!(editor.getEditorInput() instanceof FileEditorInput)) return null;
    return editor;
  }

  private XtextEditor xtextEditorFrom(IWorkbenchPartReference partRef) {
    IWorkbenchPage page = partRef.getPage();
    if (page == null) return null;
    IEditorPart activeEditor = page.getActiveEditor();
    return (activeEditor instanceof XtextEditor) ? (XtextEditor) activeEditor : null;
  }

  private void validate(XtextEditor editor) {
    final IXtextDocument document = editor.getDocument();
    if (!(document instanceof XtextDocument)) return;
    document.readOnly(new IUnitOfWork<Void, XtextResource>() {
      public java.lang.Void exec(XtextResource resource) throws Exception {
        EObject root = resource.getParseResult().getRootASTElement();
        resetImports(root);
        resource.getLinker().linkModel(root, new ListBasedDiagnosticConsumer());
        ((XtextDocument) document).checkAndUpdateAnnotations();
        return null;
      }
    });
  }

  private void resetImports(EObject root) {
    List<Import> imports = getAllContentsOfType(root, Import.class);
    for (Import anImport : imports) resetUri(anImport);
  }

  private void resetUri(Import anImport) {
    String uri = uriAsEnteredInEditor(anImport);
    if (uri == null) return;
    anImport.setImportURI(uri);
  }

  private String uriAsEnteredInEditor(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) return null;
    String text = node.getText();
    if (text == null) return null;
    return text.substring(1, text.length() - 1);
  }

  public void partBroughtToTop(IWorkbenchPartReference partRef) {}

  public void partClosed(IWorkbenchPartReference partRef) {}

  public void partDeactivated(IWorkbenchPartReference partRef) {}

  public void partOpened(IWorkbenchPartReference partRef) {}

  public void partHidden(IWorkbenchPartReference partRef) {}

  public void partVisible(IWorkbenchPartReference partRef) {}

  public void partInputChanged(IWorkbenchPartReference partRef) {}
}

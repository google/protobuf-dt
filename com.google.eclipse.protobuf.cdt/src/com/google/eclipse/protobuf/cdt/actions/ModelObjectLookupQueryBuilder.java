/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static org.eclipse.cdt.internal.ui.editor.ASTProvider.WAIT_NO;
import static org.eclipse.core.runtime.Status.*;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.model.*;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.cdt.fqn.QualifiedNameProvider;
import com.google.eclipse.protobuf.cdt.path.ProtoFilePathFinder;
import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class ModelObjectLookupQueryBuilder {
  @Inject private QualifiedNameProvider qualifiedNameProvider;
  @Inject private ProtoFilePathFinder pathFinder;

  Query buildQuery(IEditorPart editor) {
    final int offset = selectionOffsetOf(editor);
    if (offset < 0) {
      return null;
    }
    IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
    final IPath protoFilePath = pathFinder.findProtoFilePath(file);
    if (protoFilePath == null) {
      return null;
    }
    IWorkingCopy workingCopy = CUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editor.getEditorInput());
    if (workingCopy == null) {
      return null;
    }
    final AtomicReference<Query> queriesReference = new AtomicReference<Query>();
    ASTProvider astProvider = ASTProvider.getASTProvider();
    IStatus status = astProvider.runOnAST(workingCopy, WAIT_NO, null, new ASTRunnable() {
      @Override public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
        if (ast == null) {
          return CANCEL_STATUS;
        }
        IASTNodeSelector nodeSelector= ast.getNodeSelector(null);
        IASTName selectedName = nodeSelector.findEnclosingName(offset, 1);
        if (selectedName == null) {
          return CANCEL_STATUS;
        }
        if (selectedName.isDefinition()) {
          IBinding binding = selectedName.resolveBinding();
          Iterable<QualifiedName> qualifiedNames = qualifiedNameProvider.qualifiedNamesFrom(binding);
          if (qualifiedNames != null) {
            queriesReference.set(Query.newQuery(qualifiedNames, protoFilePath));
            return OK_STATUS;
          }
        }
        return CANCEL_STATUS;
      }
    });
    if (status == CANCEL_STATUS) {
      return null;
    }
    return queriesReference.get();
  }

  private int selectionOffsetOf(IEditorPart editor) {
    ISelectionProvider selectionProvider = ((ITextEditor) editor).getSelectionProvider();
    ISelection selection = selectionProvider.getSelection();
    if (selection instanceof ITextSelection) {
      ITextSelection textSelection = (ITextSelection) selection;
      return textSelection.getOffset();
    }
    return -1;
  }
}

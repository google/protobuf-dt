/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query.query;
import static java.lang.Math.max;
import static org.eclipse.cdt.internal.ui.editor.ASTProvider.WAIT_NO;
import static org.eclipse.core.runtime.Status.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.model.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
class ModelObjectDefinitionQueryBuilder {
  @Inject private ClassTypeQualifiedNameBuilder nameBuilder;
  @Inject private ProtoFilePathFinder pathFinder;

  Query buildQuery(final IEditorPart editor, final ITextSelection selection) {
    final IPath protoFilePath = pathFinder.findProtoFilePath(editor);
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
        int offset = selection.getOffset();
        int length = max(1, selection.getLength());
        IASTNodeSelector nodeSelector= ast.getNodeSelector(null);
        IASTName selectedName = nodeSelector.findEnclosingName(offset, length);
        if (selectedName == null) {
          return CANCEL_STATUS;
        }
        if (selectedName.isDefinition()) {
          IBinding binding = selectedName.resolveBinding();
          if (binding instanceof CPPClassType) {
            Collection<QualifiedName> qualifiedNames = nameBuilder.createQualifiedNamesFrom((CPPClassType) binding);
            if (qualifiedNames.isEmpty()) {
              return CANCEL_STATUS;
            }
            queriesReference.set(query(qualifiedNames, protoFilePath));
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
}

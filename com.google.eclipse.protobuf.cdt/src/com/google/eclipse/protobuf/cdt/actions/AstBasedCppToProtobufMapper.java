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
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IEditorPart;

import com.google.eclipse.protobuf.cdt.mapping.*;
import com.google.eclipse.protobuf.cdt.util.Editors;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class AstBasedCppToProtobufMapper {
  @Inject private Editors editors;
  @Inject private CppToProtobufMapper delegate;

  CppToProtobufMapping createMappingFromSelectionOf(IEditorPart editor) {
    final int offset = editors.selectionOffsetOf(editor);
    if (offset < 0) {
      return null;
    }
    IWorkingCopy workingCopy = CUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editor.getEditorInput());
    if (workingCopy == null) {
      return null;
    }
    final AtomicReference<CppToProtobufMapping> mappingReference = new AtomicReference<CppToProtobufMapping>();
    ASTProvider astProvider = ASTProvider.getASTProvider();
    IStatus status = astProvider.runOnAST(workingCopy, WAIT_NO, null, new ASTRunnable() {
      @Override public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
        if (ast == null) {
          return CANCEL_STATUS;
        }
        IASTNodeSelector nodeSelector= ast.getNodeSelector(null);
        IASTNode selectedNode = nodeSelector.findEnclosingNode(offset, 1);
        if (selectedNode instanceof IASTName) {
          IASTName selectedName = (IASTName) selectedNode;
          IBinding binding = selectedName.resolveBinding();
          CppToProtobufMapping info = delegate.createMappingFrom(binding);
          mappingReference.set(info);
          return OK_STATUS;
        }
        return CANCEL_STATUS;
      }
    });
    if (status == CANCEL_STATUS) {
      return null;
    }
    return mappingReference.get();
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.xtext.ui.editor.Messages.XtextEditorErrorTickUpdater_JobName;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.xtext.ui.PluginImageHelper;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback.NullImpl;
import org.eclipse.xtext.ui.editor.SchedulingRuleFactory;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileOutsideWorkspaceIconUpdater extends NullImpl {
  private static final ISchedulingRule SEQUENCE_RULE = SchedulingRuleFactory.INSTANCE.newSequence();

  @Inject private PluginImageHelper imageHelper;

  @Override public void afterSetInput(XtextEditor editor) {
    IEditorInput editorInput = editor.getEditorInput();
    IResource resource = (IResource) editorInput.getAdapter(IResource.class);
    if (resource == null) {
      UpdateEditorImageJob job = new UpdateEditorImageJob();
      job.scheduleFor(editor, imageHelper.getImage("pb-ro.gif"));
    }
  }

  private static class UpdateEditorImageJob extends UIJob {
    private XtextEditor editor;
    private Image titleImage;

    public UpdateEditorImageJob() {
      super(XtextEditorErrorTickUpdater_JobName);
      setRule(SEQUENCE_RULE);
    }

    @Override public IStatus runInUIThread(final IProgressMonitor monitor) {
      IEditorSite site = null != editor ? editor.getEditorSite() : null;
      if (site != null) {
        if (!monitor.isCanceled() && titleImage != null && !titleImage.isDisposed() && editor != null) {
          editor.updatedTitleImage(titleImage);
        }
      }
      return OK_STATUS;
    }

    void scheduleFor(XtextEditor newEditor, Image newTitleImage) {
      cancel();
      editor = newEditor;
      titleImage = newTitleImage;
      schedule();
    }
  }
}

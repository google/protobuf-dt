/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static org.eclipse.core.runtime.Status.OK_STATUS;

import org.eclipse.core.runtime.*;
import org.eclipse.ui.progress.UIJob;

import com.google.eclipse.protobuf.ui.editor.*;
import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NavigationJobs {
  @Inject private ModelObjectDefinitionNavigator navigator;

  void scheduleUsing(final Query query) {
    UIJob job = new UIJob("Navigating to .proto file") {
      @Override public IStatus runInUIThread(IProgressMonitor monitor) {
        navigator.navigateToDefinition(query);
        return OK_STATUS;
      }
    };
    job.schedule();
  }
}

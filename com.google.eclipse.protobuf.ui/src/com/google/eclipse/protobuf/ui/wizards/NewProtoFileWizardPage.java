/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.wizards;

import static com.google.eclipse.protobuf.ui.wizards.Messages.pageDescription;
import static com.google.eclipse.protobuf.ui.wizards.Messages.pageTitle;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NewProtoFileWizardPage extends WizardNewFileCreationPage {
  public NewProtoFileWizardPage(IStructuredSelection selection) {
    super("NewProtoFileWizardPage", selection); //$NON-NLS-1$
    setTitle(pageTitle);
    setDescription(pageDescription);
    setFileExtension("proto");
    setFileName("newFile.proto");
  }

  @Override protected InputStream getInitialContents() {
    try {
      return ProtobufActivator.getInstance().getBundle().getEntry("/resources/newFileContents.proto").openStream();
    } catch (IOException e) {
      return null;
    }
  }
}
/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.google.eclipse.protobuf.ui.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * A hyperlink for imported .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {

  private final URI importUri;
  private final String uriText;
  private final IRegion region;

  ImportHyperlink(URI importUri, String uriText, IRegion region) {
    this.importUri = importUri;
    this.uriText = uriText;
    this.region = region;
  }

  public IRegion getHyperlinkRegion() {
    return region;
  }

  public String getTypeLabel() {
    return "type";
  }

  public String getHyperlinkText() {
    return "text";
  }

  public void open() {
    String scheme = importUri.scheme();
    if ("platform".equals(scheme)) openFromWorkspace();
    if ("file".equals(scheme)) openFromFileSystem();
  }

  private void openFromWorkspace() {
    System.out.println("open from Workspace");
  }

  private void openFromFileSystem() {
    System.out.println("open from file system");
  }
}

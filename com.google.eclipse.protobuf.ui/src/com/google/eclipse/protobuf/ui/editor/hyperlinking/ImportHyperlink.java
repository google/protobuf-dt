/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;

import com.google.eclipse.protobuf.ui.editor.FileOpener;

/**
 * A hyperlink for imported .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {

  private static Logger logger = Logger.getLogger(ImportHyperlink.class);

  private final URI importUri;
  private final IRegion region;
  private final FileOpener fileOpener;

  ImportHyperlink(URI importUri, IRegion region, FileOpener fileOpener) {
    this.importUri = importUri;
    this.region = region;
    this.fileOpener = fileOpener;
  }

  public void open() {
    try {
      if (importUri.isPlatformResource()) {
        fileOpener.openProtoFileInWorkspace(importUri);
        return;
      }
      if (importUri.isPlatformPlugin()) {
        fileOpener.openProtoFileInPlugin(importUri);
        return;
      }
      if (importUri.isFile()) fileOpener.openProtoFileInFileSystem(importUri);
    } catch (PartInitException e) {
      logger.error("Unable to open " + importUri.toString(), e);
    }
  }

  public String getTypeLabel() {
    return null;
  }

  public IRegion getHyperlinkRegion() {
    return region;
  }

  public String getHyperlinkText() {
    return null;
  }
}

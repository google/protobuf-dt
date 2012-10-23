/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static org.eclipse.emf.common.util.URI.createURI;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.xtext.nodemodel.INode;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.ui.editor.FileOpener;
import com.google.inject.Inject;

/**
 * A hyperlink for imported .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {
  private static Logger logger = Logger.getLogger(ImportHyperlink.class);

  @Inject private FileOpener fileOpener;

  private URI importUri;
  private IRegion region;

  void update(Import anImport, INode importUriNode) {
    importUri = createURI(anImport.getImportURI());
    region = new Region(importUriNode.getOffset(), importUriNode.getLength());
  }

  // TODO(alruiz): Test
  @Override public void open() {
    try {
      if (importUri.isPlatformResource()) {
        fileOpener.openProtoFileInWorkspace(importUri);
        return;
      }
      if (importUri.isPlatformPlugin()) {
        fileOpener.openProtoFileInPlugin(importUri);
        return;
      }
      if (importUri.isFile()) {
        fileOpener.openProtoFileInFileSystem(importUri);
      }
    } catch (PartInitException e) {
      logger.error("Unable to open " + importUri.toString(), e);
    }
  }

  @Override public String getTypeLabel() {
    return null;
  }

  @Override public IRegion getHyperlinkRegion() {
    return region;
  }

  @Override public String getHyperlinkText() {
    return null;
  }
}

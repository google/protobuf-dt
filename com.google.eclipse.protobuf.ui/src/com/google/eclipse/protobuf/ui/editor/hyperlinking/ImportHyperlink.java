/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static com.google.eclipse.protobuf.ui.util.Resources.URI_SCHEME_FOR_FILES;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;

import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * A hyperlink for imported .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {

  private static Logger logger = Logger.getLogger(ImportHyperlink.class);
  
  private final URI importUri;
  private final IRegion region;
  private final Resources resources;

  ImportHyperlink(URI importUri, IRegion region, Resources resources) {
    this.importUri = importUri;
    this.region = region;
    this.resources = resources;
  }

  public void open() {
    String scheme = importUri.scheme();
    try {
      if ("platform".equals(scheme)) resources.openProtoFileInPlatform(importUri);
      if (URI_SCHEME_FOR_FILES.equals(scheme)) resources.openProtoFileInFileSystem(importUri);
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

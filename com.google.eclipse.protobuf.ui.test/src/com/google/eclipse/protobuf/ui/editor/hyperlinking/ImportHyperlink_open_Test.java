/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static org.eclipse.emf.common.util.URI.*;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.junit.*;

import com.google.eclipse.protobuf.ui.editor.FileOpener;

/**
 * Tests for <code>{@link ImportHyperlink#open()}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportHyperlink_open_Test {

  private static IRegion region;

  @BeforeClass public static void setUpOnce() {
    region = mock(IRegion.class);
  }
  
  private FileOpener fileOpener;
  private ImportHyperlink hyperlink;
  
  @Before public void setUp() {
    fileOpener = mock(FileOpener.class);
  }
  
  @Test public void should_open_file_in_workspace() throws Throwable {
    URI uri = createPlatformResourceURI("test.proto", false);
    hyperlink = new ImportHyperlink(uri, region, fileOpener);
    hyperlink.open();
    verify(fileOpener).openProtoFileInWorkspace(uri);
  }

  @Test public void should_open_file_in_plugin() throws Throwable {
    URI uri = createPlatformPluginURI("test.proto", false);
    hyperlink = new ImportHyperlink(uri, region, fileOpener);
    hyperlink.open();
    verify(fileOpener).openProtoFileInPlugin(uri);
  }
  
  @Test public void should_open_file_in_file_system() throws Throwable {
    URI uri = createFileURI("test.proto");
    hyperlink = new ImportHyperlink(uri, region, fileOpener);
    hyperlink.open();
    verify(fileOpener).openProtoFileInFileSystem(uri);
  }
}

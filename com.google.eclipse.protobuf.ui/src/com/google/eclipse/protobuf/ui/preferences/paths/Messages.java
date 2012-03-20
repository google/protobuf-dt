/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {
  public static String add;
  public static String addDirectoryPath;
  public static String browseFileSystem;
  public static String browseWorkspace;
  public static String directories;
  public static String directory;
  public static String directorySelection;
  public static String down;
  public static String enterDirectoryName;
  public static String errorElementIsNotDirectory;
  public static String errorEmptyDirectoryName;
  public static String errorNoDirectoryNames;
  public static String filesInMultipleDirectories;
  public static String filesInOneDirectoryOnly;
  public static String importedFilesPathResolution;
  public static String isWorkspacePathCheck;
  public static String pathResolution;
  public static String rebuildProjectNow;
  public static String remove;
  public static String selectFileSystemDirectory;
  public static String selectWorkspaceDirectory;
  public static String settingsChanged;
  public static String up;

  static {
    Class<Messages> type = Messages.class;
    NLS.initializeMessages(type.getName(), type);
  }

  private Messages() {}
}

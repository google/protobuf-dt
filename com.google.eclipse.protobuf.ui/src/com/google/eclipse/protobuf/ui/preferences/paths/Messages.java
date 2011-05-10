// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.ui.preferences.paths;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {
  
  public static String allProtosInMultipleFolders;
  public static String allProtosInOneFolder;
  public static String errorNoFolderNames;
  public static String importedFilesResolution;

  static {
    Class<Messages> targetType = Messages.class;
    NLS.initializeMessages(targetType.getName(), targetType);
  }

  private Messages() {}
}

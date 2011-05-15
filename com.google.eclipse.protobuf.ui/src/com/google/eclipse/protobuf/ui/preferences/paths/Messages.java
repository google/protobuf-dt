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
  public static String directoryNameInputMessage;
  public static String directoryNameInputTitle;
  public static String down;
  public static String errorEmptyDirectoryName;
  public static String errorNoDirectoryNames;
  public static String filesInMultipleDirectories;
  public static String filesInOneDirectoryOnly;
  public static String importedFilesPathResolution;
  public static String remove;
  public static String up;

  static {
    Class<Messages> targetType = Messages.class;
    NLS.initializeMessages(targetType.getName(), targetType);
  }

  private Messages() {}
}

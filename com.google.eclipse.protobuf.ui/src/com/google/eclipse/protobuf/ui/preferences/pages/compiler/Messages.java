/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {

  public static String browseCustomPath;
  public static String codeGeneration;
  public static String compileOnSave;
  public static String editSelected;
  public static String editCodeGenerationOptionTitle;
  public static String enabled;
  public static String errorEnterDirectoryName;
  public static String errorInvalidProtoc;
  public static String errorNoLanguageEnabled;
  public static String errorNoOutputFolderName;
  public static String errorNoSelection;
  public static String generateCpp;
  public static String generateCode;
  public static String generateJava;
  public static String generatePython;
  public static String language;
  public static String outputDirectory;
  public static String outputDirectoryPrompt;
  public static String outputFolderChildOfProjectFolder;
  public static String outputFolderName;
  public static String protocInCustomPath;
  public static String protocInSystemPath;
  public static String protocLocation;
  public static String refreshOutputProject;
  public static String refreshProject;
  public static String refreshResources;
  public static String tabMain;
  public static String tabRefresh;
  public static String targetLanguage;

  static {
    Class<Messages> targetType = Messages.class;
    NLS.initializeMessages(targetType.getName(), targetType);
  }

  private Messages() {}
}

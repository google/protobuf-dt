/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {

  static {
    Class<Messages> targetType = Messages.class;
    NLS.initializeMessages(targetType.getName(), targetType);
  }

  private Messages() {}

  public static String BasePreferencePage_enableProjectSettings;
  public static String BasePreferencePage_configureWorkspaceSettings;
  
  public static String CompilerPreferencePage_mainTab;
  public static String CompilerPreferencePage_refreshTab;
  public static String CompilerPreferencePage_browseCustomPath;
  public static String CompilerPreferencePage_compileOnSave;
  public static String CompilerPreferencePage_customPath;
  public static String CompilerPreferencePage_location;
  public static String CompilerPreferencePage_systemPath;
  public static String CompilerPreferencePage_targetLanguage;
  public static String CompilerPreferencePage_generateJava;
  public static String CompilerPreferencePage_generateCpp;
  public static String CompilerPreferencePage_generatePython;
  public static String CompilerPreferencePage_generatedCode;
  public static String CompilerPreferencePage_outputFolderName;
  public static String CompilerPreferencePage_directChildOfProjectFolder;
  public static String CompilerPreferencePage_refreshResources;
  public static String CompilerPreferencePage_refreshProject;
  public static String CompilerPreferencePage_refreshOutputProject;
  public static String CompilerPreferencePage_error_noSelection;
  public static String CompilerPreferencePage_error_invalidProtoc;
  public static String CompilerPreferencePage_error_noOutputFolderName;
}

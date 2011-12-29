/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {
  public static String comments;
  public static String defaults;
  public static String enumDefinitions;
  public static String enumLiteralDefinitions;
  public static String enumLiteralIndices;
  public static String enumLiterals;
  public static String enums;
  public static String invalidSymbols;
  public static String keywords;
  public static String messageDefinitions;
  public static String messageFieldIndices;
  public static String messages;
  public static String numbers;
  public static String punctuationCharacters;
  public static String rpcArguments;
  public static String rpcDefinitions;
  public static String rpcReturnTypes;
  public static String serviceDefinitions;
  public static String strings;

  static {
    // initialize resource bundle
    Class<Messages> type = Messages.class;
    NLS.initializeMessages(type.getName(), type);
  }

  private Messages() {}
}

/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import com.google.inject.Singleton;

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;

/**
 * Adds syntax highlighting information to tokens.
 */
@Singleton
public class ProtobufAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper {
  @Override
  protected String calculateId(String tokenName, int tokenType) {
    if ("RULE_CHUNK".equals(tokenName)) {
      return DefaultHighlightingConfiguration.STRING_ID;
    }
    return super.calculateId(tokenName, tokenType);
  }
}

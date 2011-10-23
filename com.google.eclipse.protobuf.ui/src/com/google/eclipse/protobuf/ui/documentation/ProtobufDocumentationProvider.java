/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;

import com.google.inject.Inject;

/**
 * Provides single-line and multi-line comments as documentation of a protobuf element.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentationProvider implements IEObjectDocumentationProvider {

  private final List<IEObjectDocumentationProvider> delegates = new ArrayList<IEObjectDocumentationProvider>();

  @Inject
  public ProtobufDocumentationProvider(SLCommentDocumentationProvider p1, MLCommentDocumentationProvider p2) {
    delegates.add(p1);
    delegates.add(p2);
  }

  /** {@inheritDoc} */
  @Override public String getDocumentation(EObject o) {
    for (IEObjectDocumentationProvider p: delegates) {
      String documentation = p.getDocumentation(o);
      if (!(isEmpty(documentation))) return documentation;
    }
    return "";
  }

}

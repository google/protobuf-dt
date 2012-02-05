/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.resource.ResourceDescriptions;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementMatcher {
  @Inject private IQualifiedNameConverter converter;
  @Inject private ResourceDescriptions descriptions;

  /**
   * Returns the URI of the protocol buffer element in the given resource, whose qualified name matches the one in the
   * given <code>{@link CppToProtobufMapping}</code>.
   * @param resource describes the contents of a .proto file.
   * @param mapping information of the protocol buffer element to look for.
   * @return the found URI, or {@code null} if it was not possible to find a matching protocol buffer element.
   */
  public URI findUriOfMatchingProtobufElement(IResourceDescription resource, CppToProtobufMapping mapping) {
    QualifiedName qualifiedName = mapping.qualifiedName();
    Pattern pattern = patternToMatchFrom(qualifiedName);
    List<IEObjectDescription> matches = descriptions.matchingQualifiedNames(resource, pattern);
    if (matches.size() == 1) {
      return matches.get(0).getEObjectURI();
    }
    return null;
  }

  Pattern patternToMatchFrom(QualifiedName qualifiedName) {
    String qualifiedNameAsText = converter.toString(qualifiedName);
    String regex = Pattern.quote(qualifiedNameAsText.replaceAll("_", "."));
    return Pattern.compile(regex);
  }
}

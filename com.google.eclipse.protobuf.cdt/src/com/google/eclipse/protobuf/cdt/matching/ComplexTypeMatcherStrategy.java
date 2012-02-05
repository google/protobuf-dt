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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.*;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.resource.ResourceDescriptions;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ComplexTypeMatcherStrategy implements ProtobufElementMatcherStrategy {
  @Inject private IQualifiedNameConverter converter;
  @Inject private ResourceDescriptions descriptions;

  @Override public URI findUriOfMatchingProtobufElement(IResourceDescription resource, CppToProtobufMapping mapping) {
    String qualifiedNameAsText = converter.toString(mapping.qualifiedName());
    String regex = Pattern.quote(qualifiedNameAsText.replaceAll("_", "."));
    Pattern pattern = Pattern.compile(regex);
    List<IEObjectDescription> matches = descriptions.matchingQualifiedNames(resource, pattern);
    if (matches.size() == 1) {
      return matches.get(0).getEObjectURI();
    }
    return null;
  }

  @Override public boolean canHandle(Class<? extends EObject> protobufElementType) {
    return protobufElementType.equals(Message.class) || protobufElementType.equals(Enum.class);
  }
}

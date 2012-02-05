/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ResourceDescriptions#modelObjectUri(IResourceDescription, QualifiedName)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceDescriptions_modelObjectUri_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IQualifiedNameConverter fqnConverter;
  @Inject private ResourceSetBasedResourceDescriptions index;
  @Inject private ResourceDescriptions resourceDescriptions;

  // syntax = "proto2";
  // package com.google.proto;
  //
  // enum Type {
  //   ONE = 1;
  //   TWO = 2;
  // }
  @Test public void should_find_URI_of_model_object_given_its_qualified_name() {
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Type");
    URI foundUri = resourceDescriptions.modelObjectUri(descriptionOf(xtext.resource()), qualifiedName);
    assertThat(foundUri, equalTo(uriOfEnumWithName("Type")));
  }

  private URI uriOfEnumWithName(String name) {
    XtextResource resource = xtext.resource();
    Enum anEnum = xtext.find("Type", Enum.class);
    String fragment = resource.getURIFragment(anEnum);
    return resource.getURI().appendFragment(fragment);
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Person {}
  @Test public void should_return_null_if_file_name_is_equal_but_file_path_is_not() {
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Type");
    URI foundUri = resourceDescriptions.modelObjectUri(descriptionOf(xtext.resource()), qualifiedName);
    assertNull(foundUri);
  }

  private IResourceDescription descriptionOf(Resource resource) {
    index.setContext(resource);
    return index.getResourceDescription(resource.getURI());
  }
}

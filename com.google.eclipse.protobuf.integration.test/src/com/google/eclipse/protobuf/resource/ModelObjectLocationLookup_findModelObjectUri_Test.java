/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ModelObjectLocationLookup#findModelObjectUri(Iterable, IPath)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjectLocationLookup_findModelObjectUri_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private IQualifiedNameConverter fqnConverter;
  @Inject private ModelObjectLocationLookup lookup;

  // syntax = "proto2";
  // package com.google.proto;
  //
  // enum Type {
  //   ONE = 1;
  //   TWO = 2;
  // }
  @Test public void should_find_URI_of_model_object_given_its_qualified_name() {
    XtextResource resource = xtext.resource();
    addToXtextIndex(resource);
    Iterable<QualifiedName> qualifiedNames = singletonList(fqnConverter.toQualifiedName("com.google.proto.Type"));
    URI foundUri = lookup.findModelObjectUri(qualifiedNames, pathOf(resource));
    Enum anEnum = xtext.find("Type", Enum.class);
    String fragment = resource.getURIFragment(anEnum);
    URI expectedUri = resource.getURI().appendFragment(fragment);
    assertThat(foundUri, equalTo(expectedUri));
  }

  private IPath pathOf(XtextResource resource) {
    return new Path(resource.getURI().path());
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Person {}
  @Test public void should_return_null_if_file_name_is_equal_but_file_path_is_not() {
    addToXtextIndex(xtext.resource());
    Iterable<QualifiedName> qualifiedNames = singletonList(fqnConverter.toQualifiedName("com.google.proto.Person"));
    URI foundUri = lookup.findModelObjectUri(qualifiedNames, new Path("/test/src/protos/mytestmodel.proto"));
    assertNull(foundUri);
  }

  private void addToXtextIndex(XtextResource resource) {
    IResourceDescriptions xtextIndex = lookup.getXtextIndex();
    if (xtextIndex instanceof ResourceSetBasedResourceDescriptions) {
      ((ResourceSetBasedResourceDescriptions) xtextIndex).setContext(resource);
    }
  }
}

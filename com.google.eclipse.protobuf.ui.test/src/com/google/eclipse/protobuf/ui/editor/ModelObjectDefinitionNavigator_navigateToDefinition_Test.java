/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query.newQuery;
import static java.util.Collections.singletonList;
import static org.eclipse.core.runtime.Status.*;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.eclipse.protobuf.resource.*;
import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ModelObjectDefinitionNavigator#navigateToDefinition(Query)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjectDefinitionNavigator_navigateToDefinition_Test {
  private static IPath filePath;

  @BeforeClass public static void setUpOnce() {
    filePath = new Path("/src/protos/test.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private IURIEditorOpener editorOpener;
  @Inject private IQualifiedNameConverter fqnConverter;
  @Inject private IndexLookup indexLookup;
  @Inject private ResourceDescriptions resources;
  @Inject private ModelObjectDefinitionNavigator navigator;

  private IResourceDescription resource;

  @Before public void setUp() {
    resource = mock(IResourceDescription.class);
  }

  @Test public void should_navigate_to_model_object_if_URI_is_found() {
    when(indexLookup.resourceIn(filePath)).thenReturn(resource);
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Type");
    URI uri = createURI("file:/usr/local/project/src/protos/test.proto");
    when(resources.modelObjectUri(resource, qualifiedName)).thenReturn(uri);
    IStatus result = navigator.navigateToDefinition(newQuery(singletonList(qualifiedName), filePath));
    assertThat(result, equalTo(OK_STATUS));
    verify(editorOpener).open(uri, true);
  }

  @Test public void should_not_navigate_to_model_object_if_URI_is_not_found() {
    when(indexLookup.resourceIn(filePath)).thenReturn(resource);
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Person");
    when(resources.modelObjectUri(resource, qualifiedName)).thenReturn(null);
    IStatus result = navigator.navigateToDefinition(newQuery(singletonList(qualifiedName), filePath));
    assertThat(result, equalTo(CANCEL_STATUS));
    verifyZeroInteractions(editorOpener);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(IURIEditorOpener.class);
      mockAndBind(IndexLookup.class);
      mockAndBind(ResourceDescriptions.class);
    }
  }
}

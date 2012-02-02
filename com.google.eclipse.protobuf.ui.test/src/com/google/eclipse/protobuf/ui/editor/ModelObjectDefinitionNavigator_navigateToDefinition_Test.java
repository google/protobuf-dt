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
import static com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query.query;
import static org.eclipse.core.runtime.Status.*;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.eclipse.protobuf.resource.ModelObjectLocationLookup;
import com.google.eclipse.protobuf.ui.editor.ModelObjectDefinitionNavigator.Query;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link ModelObjectDefinitionNavigator#navigateToDefinition(Query)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjectDefinitionNavigator_navigateToDefinition_Test {
  private static IPath filePath;

  @BeforeClass public static void setUp() {
    filePath = new Path("/src/protos/test.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private IQualifiedNameConverter fqnConverter;
  @Inject private ModelObjectLocationLookup locationLookup;
  @Inject private IURIEditorOpener editorOpener;
  @Inject private ModelObjectDefinitionNavigator navigator;

  @Test public void should_navigate_to_model_object_if_URI_is_found() {
    URI uri = createURI("file:/usr/local/project/src/protos/test.proto");
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Type");
    when(locationLookup.findModelObjectUri(qualifiedName, filePath)).thenReturn(uri);
    IStatus result = navigator.navigateToDefinition(query(qualifiedName, filePath));
    assertThat(result, equalTo(OK_STATUS));
    verify(editorOpener).open(uri, true);
  }

  @Test public void should_not_navigate_to_model_object_if_URI_is_not_found() {
    QualifiedName qualifiedName = fqnConverter.toQualifiedName("com.google.proto.Person");
    when(locationLookup.findModelObjectUri(qualifiedName, filePath)).thenReturn(null);
    IStatus result = navigator.navigateToDefinition(query(qualifiedName, filePath));
    assertThat(result, equalTo(CANCEL_STATUS));
    verifyZeroInteractions(editorOpener);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(ModelObjectLocationLookup.class);
      mockAndBind(IURIEditorOpener.class);
    }
  }
}

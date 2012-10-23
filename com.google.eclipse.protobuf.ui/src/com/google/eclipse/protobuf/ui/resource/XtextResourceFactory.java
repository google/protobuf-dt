/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.resource;

import static java.util.Collections.singletonMap;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.resource.ContentHandler.UNSPECIFIED_CONTENT_TYPE;
import static org.eclipse.xtext.EcoreUtil2.resolveLazyCrossReferences;
import static org.eclipse.xtext.resource.XtextResource.OPTION_ENCODING;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import static com.google.eclipse.protobuf.util.Encodings.UTF_8;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.ui.util.IProjects;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Factory of <code>{@link XtextResource}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class XtextResourceFactory {
  @Inject private IResourceSetProvider resourceSetProvider;
  @Inject private IProjects projects;

  /**
   * Creates a new <code>{@link XtextResource}</code>.
   * @param uri the URI of the file containing the EMF model.
   * @param contents the contents of the file.
   * @return the created {@code XtextResource}.
   * @throws IOException if something goes wrong.
   */
  public XtextResource createResource(String uri, String contents) throws IOException {
    return createResource(createURI(uri), contents);
  }

  /**
   * Creates a new <code>{@link XtextResource}</code>.
   * @param uri the URI of the file containing the EMF model.
   * @param contents the contents of the file.
   * @return the created {@code XtextResource}.
   * @throws IOException if something goes wrong.
   */
  public XtextResource createResource(URI uri, String contents) throws IOException {
    // TODO get project from URI.
    ResourceSet resourceSet = resourceSetProvider.get(projects.activeProject());
    XtextResource resource = (XtextResource) resourceSet.createResource(uri, UNSPECIFIED_CONTENT_TYPE);
    resource.load(new StringInputStream(contents), singletonMap(OPTION_ENCODING, UTF_8));
    resolveLazyCrossReferences(resource, NullImpl);
    return resource;
  }
}

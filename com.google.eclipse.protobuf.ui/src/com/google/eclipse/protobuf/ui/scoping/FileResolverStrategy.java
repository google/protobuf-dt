/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
interface FileResolverStrategy {
  String resolveUri(String importUri, URI declaringResourceUri, Iterable<PathsPreferences> allPathPreferences);
}

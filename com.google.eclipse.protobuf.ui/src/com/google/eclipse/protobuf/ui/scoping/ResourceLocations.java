/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.ui.util.IPaths.directoryLocationInWorkspace;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ResourceLocations {
  String directoryLocation(String workspacePath) {
    IPath path = Path.fromOSString(workspacePath);
    return directoryLocationInWorkspace(path);
  }
}

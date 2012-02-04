/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc.command;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
interface OutputDirectoryProtocOption extends ProtocOption {
  IFolder outputDirectory() throws CoreException;
}

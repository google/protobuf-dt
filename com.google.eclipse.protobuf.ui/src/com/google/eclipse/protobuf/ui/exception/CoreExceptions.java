/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.exception;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static org.eclipse.core.runtime.IStatus.ERROR;

import org.eclipse.core.runtime.*;

/**
 * Factory of <code>{@link CoreException}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class CoreExceptions {
  public static CoreException error(Throwable cause) {
    String message = nullToEmpty(cause.getMessage());
    return new CoreException(new Status(ERROR, PLUGIN_ID, message, cause));
  }

  private CoreExceptions() {}
}

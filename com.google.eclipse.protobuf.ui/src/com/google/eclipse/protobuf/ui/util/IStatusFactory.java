/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.eclipse.core.runtime.IStatus.ERROR;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn.protobufPluginId;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Factory of <code>{@link IStatus}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IStatusFactory {
  public static IStatus error(Throwable cause) {
    String message = nullToEmpty(cause.getMessage());
    return error(message, cause);
  }

  public static IStatus error(String message, Throwable cause) {
    return new Status(ERROR, protobufPluginId(), message, cause);
  }

  public static IStatus error(String message) {
    return new Status(ERROR, protobufPluginId(), message);
  }

  private IStatusFactory() {}
}

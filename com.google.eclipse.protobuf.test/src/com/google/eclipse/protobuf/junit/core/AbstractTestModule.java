/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class AbstractTestModule extends AbstractModule {
  protected <T> void mockAndBind(Class<T> classToMock) {
    binder().bind(classToMock).toInstance(mock(classToMock));
  }
}

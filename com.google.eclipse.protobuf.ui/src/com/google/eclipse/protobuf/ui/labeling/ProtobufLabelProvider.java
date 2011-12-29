/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a {@code EObject}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider">Xtext Label Provider</a>
 */
public class ProtobufLabelProvider extends DefaultEObjectLabelProvider {
  @Inject private Labels labels;
  @Inject private Images images;

  @Inject public ProtobufLabelProvider(AdapterFactoryLabelProvider delegate) {
    super(delegate);
  }

  @Override public Object text(Object o) {
    Object text = labels.labelFor(o);
    return (text != null) ? text : super.text(o);
  }

  @Override public Object image(Object o) {
    return images.imageFor(o);
  }
}

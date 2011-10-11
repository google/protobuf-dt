/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.hamcrest.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IEObjectDescriptionHasName extends BaseMatcher<IEObjectDescription> {

  private final String qualifiedName;

  public static IEObjectDescriptionHasName hasName(String name) {
    return new IEObjectDescriptionHasName(name);
  }
  
  private IEObjectDescriptionHasName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }
  
  /** {@inheritDoc} */
  public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescription)) return false;
    IEObjectDescription description = (IEObjectDescription) arg;
    QualifiedName actual = description.getName();
    return (actual == null) ? false : qualifiedName.equals(actual.toString());
  }
  
  /** {@inheritDoc} */
  public void describeTo(Description description) {
    description.appendValue(qualifiedName);
  }
}

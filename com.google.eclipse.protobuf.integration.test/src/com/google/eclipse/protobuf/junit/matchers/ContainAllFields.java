/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.*;

import com.google.eclipse.protobuf.junit.IEObjectDescriptions;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllFields extends BaseMatcher<IEObjectDescriptions> {

  private final Collection<MessageField> fields = new ArrayList<MessageField>();

  public static ContainAllFields containAll(Collection<MessageField> fields) {
    return new ContainAllFields(fields);
  }
  
  private ContainAllFields(Collection<MessageField> fields) {
    this.fields.addAll(fields);
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    if (descriptions.size() != fields.size()) return false;
    for (MessageField field : fields) {
      String name = field.getName();
      EObject described = descriptions.objectDescribedAs(name);
      if (described != field) return false;
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    List<String> names = new ArrayList<String>();
    for (MessageField field : fields) {
      names.add(field.getName());
    }
    description.appendValue(names);
  }
}

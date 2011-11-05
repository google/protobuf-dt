/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.*;

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ContainAllFieldsInMessage extends BaseMatcher<IEObjectDescriptions> {

  private final EObject container;

  static ContainAllFieldsInMessage containAllFieldsIn(Group group) {
    return new ContainAllFieldsInMessage(group);
  }

  static ContainAllFieldsInMessage containAllFieldsIn(Message message) {
    return new ContainAllFieldsInMessage(message);
  }

  private ContainAllFieldsInMessage(EObject container) {
    this.container = container;
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    List<Field> fields = allFields();
    if (descriptions.size() != fields.size()) return false;
    for (Field field : fields) {
      String name = field.getName();
      EObject described = descriptions.objectDescribedAs(name);
      if (described != field) return false;
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    List<String> names = new ArrayList<String>();
    for (Field field : allFields()) {
      names.add(field.getName());
    }
    description.appendValue(names);
  }

  private List<Field> allFields() {
    return getAllContentsOfType(container, Field.class);
  }
}

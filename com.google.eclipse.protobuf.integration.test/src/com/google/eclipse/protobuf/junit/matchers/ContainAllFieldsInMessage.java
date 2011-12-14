/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.*;

import com.google.eclipse.protobuf.junit.IEObjectDescriptions;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllFieldsInMessage extends BaseMatcher<IEObjectDescriptions> {

  private final EObject container;

  public static ContainAllFieldsInMessage containAllFieldsIn(Group group) {
    return new ContainAllFieldsInMessage(group);
  }

  public static ContainAllFieldsInMessage containAllFieldsIn(Message message) {
    return new ContainAllFieldsInMessage(message);
  }

  private ContainAllFieldsInMessage(EObject container) {
    this.container = container;
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    List<IndexedElement> elements = allIndexedElements();
    if (descriptions.size() != elements.size()) return false;
    for (IndexedElement e : elements) {
      String name = nameOf(e);
      EObject described = descriptions.objectDescribedAs(name);
      if (described != e) return false;
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    List<String> names = new ArrayList<String>();
    for (IndexedElement e : allIndexedElements()) {
      names.add(nameOf(e));
    }
    description.appendValue(names);
  }

  private List<IndexedElement> allIndexedElements() {
    return getAllContentsOfType(container, IndexedElement.class);
  }

  private String nameOf(IndexedElement e) {
    if (e == null) return null;
    return (e instanceof Group) ? ((Group) e).getName() : ((MessageField) e).getName();
  }
}

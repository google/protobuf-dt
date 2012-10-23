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

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.junit.IEObjectDescriptions;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllFieldsInMessage extends TypeSafeMatcher<IEObjectDescriptions> {
  private final EObject container;

  public static ContainAllFieldsInMessage containAllFieldsIn(Group group) {
    return new ContainAllFieldsInMessage(group);
  }

  public static ContainAllFieldsInMessage containAllFieldsIn(Message message) {
    return new ContainAllFieldsInMessage(message);
  }

  private ContainAllFieldsInMessage(EObject container) {
    super(IEObjectDescriptions.class);
    this.container = container;
  }

  @Override public boolean matchesSafely(IEObjectDescriptions item) {
    List<IndexedElement> elements = allIndexedElements();
    if (item.size() != elements.size()) {
      return false;
    }
    for (IndexedElement e : elements) {
      String name = nameOf(e);
      EObject described = item.objectDescribedAs(name);
      if (described != e) {
        return false;
      }
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    List<IndexedElement> allIndexedElements = allIndexedElements();
    Collection<String> names = transform(allIndexedElements, new Function<IndexedElement, String>() {
      @Override public String apply(IndexedElement input) {
        return nameOf(input);
      }
    });
    description.appendValue(names);
  }

  private List<IndexedElement> allIndexedElements() {
    return getAllContentsOfType(container, IndexedElement.class);
  }

  private String nameOf(IndexedElement e) {
    if (e == null) {
      return null;
    }
    return (e instanceof Group) ? ((Group) e).getName() : ((MessageField) e).getName();
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.junit.IEObjectDescriptions;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllFields extends TypeSafeMatcher<IEObjectDescriptions> {
  private final Collection<MessageField> fields = newArrayList();

  public static ContainAllFields containAll(Collection<MessageField> fields) {
    return new ContainAllFields(fields);
  }

  private ContainAllFields(Collection<MessageField> fields) {
    super(IEObjectDescriptions.class);
    this.fields.addAll(fields);
  }

  @Override public boolean matchesSafely(IEObjectDescriptions item) {
    if (item.size() != fields.size()) {
      return false;
    }
    for (MessageField field : fields) {
      String name = field.getName();
      EObject described = item.objectDescribedAs(name);
      if (described != field) {
        return false;
      }
    }
    return true;
  }

  @Override public void describeTo(Description description) {
    Collection<String> names = transform(fields, new Function<MessageField, String>() {
      @Override public String apply(MessageField input) {
        return input.getName();
      }
    });
    description.appendValue(names);
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;
import static java.lang.Math.max;
import static java.util.Collections.emptyList;

import java.util.List;

import org.eclipse.emf.ecore.*;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IndexedElement}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class IndexedElements {

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given {@code IndexedElement}, or {@code null} if the given {@code IndexedElement} is
   * {@code null}.
   */
  public String nameOf(IndexedElement e) {
    if (e instanceof MessageField) {
      MessageField field = (MessageField) e;
      return field.getName();
    }
    if (e instanceof Group) {
      Group group = (Group) e;
      return group.getName();
    }
    return null;
  }

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given {@code IndexedElement}, or {@code Long.MIN_VALUE} if the given {@code IndexedElement}
   * is {@code null}.
   */
  public long indexOf(IndexedElement e) {
    if (e instanceof Group) {
      return ((Group) e).getIndex();
    }
    if (e instanceof MessageField) {
      return ((MessageField) e).getIndex();
    }
    return Long.MIN_VALUE;
  }

  /**
   * Returns the "index" feature of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the "index" feature of the given {@code IndexedElement}, or {@code null} if the given
   * {@code IndexedElement} is {@code null}.
   */
  public EStructuralFeature indexFeatureOf(IndexedElement e) {
    if (e instanceof Group) {
      return GROUP__INDEX;
    }
    if (e instanceof MessageField) {
      return MESSAGE_FIELD__INDEX;
    }
    return null;
  }

  /**
   * Returns the options of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the options of the given {@code IndexedElement}, or an empty list if the given {@code IndexedElement} is
   * {@code null}.
   */
  public List<FieldOption> fieldOptionsOf(IndexedElement e) {
    if (e instanceof Group) {
      return ((Group) e).getFieldOptions();
    }
    if (e instanceof MessageField) {
      return ((MessageField) e).getFieldOptions();
    }
    return emptyList();
  }

  /**
   * Sets the index of the given <code>{@link IndexedElement}</code>.
   * @param e e the given {@code IndexedElement}.
   * @param newIndex the new index to set.
   */
  public void setIndexTo(IndexedElement e, long newIndex) {
    if (e instanceof Group) {
      ((Group) e).setIndex(newIndex);
    }
    if (e instanceof MessageField) {
      ((MessageField) e).setIndex(newIndex);
    }
  }

  /**
   * Calculates the tag number value for the given element. The calculated tag number value is the maximum of all the
   * tag number values of the given element's siblings, plus one. The minimum tag number value is 1.
   * <p>
   * For example, in the following message:
   *
   * <pre>
   * message Person {
   *   required string name = 1;
   *   optional string email = 2;
   *   optional PhoneNumber phone =
   * </pre>
   *
   * The calculated tag number value for the element {@code PhoneNumber} will be 3.
   * </p>
   * @param e the given element.
   * @return the calculated value for the tag number of the given element.
   */
  public long calculateTagNumberOf(IndexedElement e) {
    long index = 0;
    for (EObject o : e.eContainer().eContents()) {
      if (o == e || !(o instanceof IndexedElement)) {
        continue;
      }
      index = max(index, indexOf((IndexedElement) o));
    }
    return ++index;
  }
}

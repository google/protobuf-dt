/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.lang.Math.max;
import static java.util.Collections.emptyList;

import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.SimpleAttributeResolver;

import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IndexedElement}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class IndexedElements {
  private final static SimpleAttributeResolver<EObject, Long> INDEX_RESOLVER = newResolver(long.class, "index");

  @Inject private ModelObjects modelObjects;

  /**
   * Calculates the index value for the given element. The calculated index value is the maximum of all the index values
   * of the given element's siblings, plus one. The minimum index value is 1.
   * <p>
   * For example, in the following message:
   * <pre>
   * message Person {
   *   required string name = 1;
   *   optional string email = 2;
   *   optional PhoneNumber phone =
   * </pre>
   * The calculated index value for the element {@code PhoneNumber} will be 3.
   * </p>
   * @param e the given element.
   * @return the calculated value for the index of the given element.
   */
  public long calculateNewIndexFor(IndexedElement e) {
    EObject type = e.eContainer();
    long index = findMaxIndex(type.eContents());
    return ++index;
  }
  
  private long findMaxIndex(Iterable<? extends EObject> elements) {
    long maxIndex = 0;

    for (EObject e : elements) {
      if (e instanceof OneOf) {
        maxIndex = max(maxIndex, findMaxIndex(((OneOf) e).getElements()));
      } else if (e instanceof IndexedElement) {
        maxIndex  = max(maxIndex, indexOf((IndexedElement) e));
      }
    }
    
    return maxIndex;
  }

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given {@code IndexedElement}, or {@code Long.MIN_VALUE} if the given {@code IndexedElement}
   * is {@code null}.
   */
  public long indexOf(IndexedElement e) {
    long index = Long.MIN_VALUE;
    EStructuralFeature feature = indexFeatureOf(e);
    if (feature != null) {
      index = (Long) e.eGet(feature);
    }
    return index;
  }

  /**
   * Returns the options of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the options of the given {@code IndexedElement}, or an empty list if the given {@code IndexedElement} is
   * {@code null}.
   */
  @SuppressWarnings("unchecked")
  public List<FieldOption> fieldOptionsOf(IndexedElement e) {
    List<FieldOption> options = modelObjects.valueOfFeature(e, "fieldOptions", List.class);
    if (options == null) {
      options = emptyList();
    }
    return options;
  }

  /**
   * Sets the index of the given <code>{@link IndexedElement}</code>.
   * @param e e the given {@code IndexedElement}.
   * @param newIndex the new index to set.
   */
  public void setIndexTo(IndexedElement e, long newIndex) {
    EStructuralFeature feature = indexFeatureOf(e);
    if (feature != null) {
      e.eSet(feature, newIndex);
    }
  }

  /**
   * Returns the "index" structural feature of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the "index" structural feature of the given {@code IndexedElement}, or {@code null} if the given
   * {@code IndexedElement} is {@code null}.
   */
  public EStructuralFeature indexFeatureOf(IndexedElement e) {
    return (e != null) ? INDEX_RESOLVER.getAttribute(e) : null;
  }
}

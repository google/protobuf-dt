/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static java.util.Collections.emptyMap;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.contentassist.IEObjectDescriptionsHaveNames.containOnly;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.protobuf.Message;

/**
 * Tests for <code>{@link IEObjectDescriptionChooser#shortestQualifiedNamesIn(Collection)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IEObjectDescriptionChooser_shortestQualifiedNamesIn_Test {
  private List<IEObjectDescription> descriptions;
  private Map<String, String> userData;
  private IEObjectDescriptionChooser chooser;

  @Before public void setUp() {
    userData = emptyMap();
    chooser = new IEObjectDescriptionChooser();
    descriptions = newArrayList();
    describe(mock(Message.class), QualifiedName.create("com", "google", "test", "Phone"));
    describe(mock(Message.class), QualifiedName.create("com", "google", "test", "EMail"));
  }

  /*
   * Creates IEObjectDescriptions for the given EObject, one per segment in the given qualified name.
   *
   * Example:
   * Given the qualified name "com.google.test.Phone", this method will use these qualified names to create
   * IEObjectDescriptions:
   * - "Phone"
   * - "test.Phone"
   * - "google.test.Phone"
   * - "com.google.test.Phone"
   */
  private void describe(EObject e, QualifiedName name) {
    int count = name.getSegmentCount();
    List<String> segments = newArrayList();
    for (int i = count - 1; i >= 0; i--) {
      segments.add(0, name.getSegment(i));
      QualifiedName newName = QualifiedName.create(segments.toArray(new String[segments.size()]));
      descriptions.add(new EObjectDescription(newName, e, userData));
    }
  }

  @Test public void should_return_descriptions_with_shortest_QualifiedName() {
    Collection<IEObjectDescription> chosen = chooser.shortestQualifiedNamesIn(descriptions);
    assertThat(chosen, containOnly("EMail", "Phone"));
  }
}

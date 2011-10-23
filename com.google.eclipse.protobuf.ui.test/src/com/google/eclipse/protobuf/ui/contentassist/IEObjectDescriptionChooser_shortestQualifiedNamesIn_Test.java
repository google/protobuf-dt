/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.eclipse.protobuf.ui.contentassist.IEObjectDescriptionsHaveNames.containOnly;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.protobuf.Message;

/**
 * Tests for <code>{@link IEObjectDescriptionChooser#shortestQualifiedNamesIn(IScope)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IEObjectDescriptionChooser_shortestQualifiedNamesIn_Test {

  private IScope scope;
  private List<IEObjectDescription> descriptions;
  private Map<String, String> userData;
  private IEObjectDescriptionChooser chooser;
  
  @Before public void setUp() {
    scope = mock(IScope.class);
    userData = emptyMap();
    chooser = new IEObjectDescriptionChooser();
    descriptions = new ArrayList<IEObjectDescription>();
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
    List<String> segments = new ArrayList<String>();
    for (int i = count - 1; i >= 0; i--) {
      segments.add(0, name.getSegment(i));
      QualifiedName newName = QualifiedName.create(segments.toArray(new String[segments.size()]));
      descriptions.add(new EObjectDescription(newName, e, userData));
    }
  }
  
  @Test public void should_return_descriptions_with_shortest_QualifiedName() {
    when(scope.getAllElements()).thenReturn(descriptions);
    Collection<IEObjectDescription> chosen = chooser.shortestQualifiedNamesIn(scope);
    assertThat(chosen, containOnly("EMail", "Phone"));
  }
}

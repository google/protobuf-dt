/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.eclipse.protobuf.ui.contentassist.IEObjectDescriptionHasName.hasName;
import static java.util.Collections.emptyMap;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.protobuf.Message;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import java.util.*;

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
    List<IEObjectDescription> chosen = new ArrayList<IEObjectDescription>(chooser.shortestQualifiedNamesIn(scope));
    assertThat(chosen.size(), equalTo(2));
    assertThat(chosen.get(0), hasName("Phone"));
    assertThat(chosen.get(1), hasName("EMail"));
  }
}

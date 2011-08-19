/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.matchers.PropertyHasType.hasType;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;

/**
 * Tests for <code>{@link ProtoDescriptor#fileOptions()}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Ignore("This test requires to be executed as a 'JUnit plug-in test'. I haven't found a way to register PlatformURLHandler with the JVM")
public class Descriptor_fileOptions_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ProtoDescriptor descriptor;

  @Before public void setUp() {
    descriptor = xtext.getInstanceOf(ProtoDescriptorProvider.class).get();
  }

  @Test public void should_return_all_file_options() {
    Map<String, Property> fileOptions = mapByName(descriptor.fileOptions());
    assertThat(fileOptions.get("java_package"), hasType("string"));
    assertThat(fileOptions.get("java_outer_classname"), hasType("string"));
    assertThat(fileOptions.get("java_multiple_files"), hasType("bool"));
    assertThat(fileOptions.get("java_generate_equals_and_hash"), hasType("bool"));
    assertThat(fileOptions.containsKey("optimize_for"), equalTo(true));
    assertThat(fileOptions.get("cc_generic_services"), hasType("bool"));
    assertThat(fileOptions.get("java_generic_services"), hasType("bool"));
    assertThat(fileOptions.get("py_generic_services"), hasType("bool"));
  }

  private static Map<String, Property> mapByName(Collection<Property> properties) {
    Map<String, Property> mapByName = new HashMap<String, Property>();
    for (Property property : properties)
      mapByName.put(property.getName(), property);
    return mapByName;
  }
}

/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static com.google.common.base.Objects.equal;

import java.util.Arrays;

import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufDiagnosticMatcher extends TypeSafeMatcher<ProtobufDiagnostic> {
  private final DiagnosticMessage message;

  static ProtobufDiagnosticMatcher wasCreatedFrom(DiagnosticMessage message) {
    return new ProtobufDiagnosticMatcher(message);
  }

  private ProtobufDiagnosticMatcher(DiagnosticMessage message) {
    super(ProtobufDiagnostic.class);
    this.message = message;
  }

  @Override public boolean matchesSafely(ProtobufDiagnostic item) {
    return equal(message.getIssueCode(), item.getCode()) &&
        Arrays.equals(message.getIssueData(), item.getData()) &&
        equal(message.getMessage(), item.getMessage());
  }

  @Override public void describeTo(Description description) {
    description.appendText(describeMessage());
  }

  private String describeMessage() {
    String format = "[issueCode=%s, issueData=%s, message='%s']";
    return String.format(format, message.getIssueCode(), Arrays.toString(message.getIssueData()), message.getMessage());
  }
}

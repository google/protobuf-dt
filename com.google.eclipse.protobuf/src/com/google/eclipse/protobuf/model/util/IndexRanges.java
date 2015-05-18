/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.common.collect.Range;
import com.google.eclipse.protobuf.protobuf.IndexRange;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IndexRange}</code>.
 *
 * @author jogl@google.com (John Glassmyer)
 */
@Singleton public class IndexRanges {
  private ProtobufGrammarAccess protobufGrammarAccess;

  @Inject
  IndexRanges(ProtobufGrammarAccess protobufGrammarAccess) {
    this.protobufGrammarAccess = protobufGrammarAccess;
  }

  public Range<Long> toLongRange(IndexRange indexRange) {
    long from = indexRange.getFrom();

    Range<Long> range;
    String toString = indexRange.getTo();
    if (toString == null) {
      range = Range.singleton(from);
    } else if (toString.equals(getMaxKeyword())) {
      range = Range.atLeast(from);
    } else {
      range = Range.closed(from, Long.valueOf(toString));
    }

    return range;
  }

  public String getMaxKeyword() {
    return protobufGrammarAccess.getIndexRangeMaxAccess().getMaxKeyword_1().getValue();
  }
}

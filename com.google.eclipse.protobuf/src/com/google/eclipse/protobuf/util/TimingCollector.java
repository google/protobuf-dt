/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

public class TimingCollector {
  private long invocationCount;
  private long sum;
  private long time;

  public double getAverageInMilliseconds() {
    return sum * 1.e-6 / invocationCount;
  }

  public long getInvocationCount() {
    return invocationCount;
  }

  public void startTimer() {
    time = System.nanoTime();
  }

  public void stopTimer() {
    sum += System.nanoTime() - time;
    invocationCount++;
  }

  public void clear() {
    invocationCount = 0;
    sum = 0;
  }

  @Override
  public String toString() {
    return String.format(
        "Invocation Count: %1$d Mean Duration: %2$fms",
        getInvocationCount(),
        getAverageInMilliseconds());
  }
}

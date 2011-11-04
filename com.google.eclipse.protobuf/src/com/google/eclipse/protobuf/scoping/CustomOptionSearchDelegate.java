/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionSearchDelegate extends ExtendMessageSearchDelegate {

  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria) {
    OptionType optionType = optionTypeFrom(criteria);
    return super.descriptions(target, optionType.messageName());
  }

  @Override public Collection<IEObjectDescription> descriptions(Object target, Object criteria, int level) {
    OptionType optionType = optionTypeFrom(criteria);
    return super.descriptions(target, optionType.messageName(), level);
  }

  private OptionType optionTypeFrom(Object criteria) {
    if (!(criteria instanceof OptionType)) 
      throw new IllegalArgumentException("Search criteria should be OptionType");
    return (OptionType) criteria;
  }
}

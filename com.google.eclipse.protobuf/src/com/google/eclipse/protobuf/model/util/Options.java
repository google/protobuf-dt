/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.model.OptionType;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Option}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Options {

  /**
   * Returns the <code>{@link Property}</code> the given <code>{@link Option}</code> is referring to. In the
   * following example
   * <pre>
   * option (myOption) = true;
   * </pre>
   * this method will return the <code>{@link Property}</code> "myOption" is pointing to.
   * @param option the given {@code Option}.
   * @return the {@code Property} the given {@code Option} is referring to, or {@code null} if it cannot be
   * found.
   */
  public Property propertyFrom(Option option) {
    PropertyRef ref = option.getProperty();
    return (ref == null) ? null : ref.getProperty();
  }

  /**
   * Returns the field of the <code>{@link Property}</code> the given <code>{@link CustomOption}</code> is referring to. 
   * In the following example
   * <pre>
   * option (myOption).field = true;
   * </pre>
   * this method will return the <code>{@link Property}</code> "field" is pointing to.
   * @param option the given {@code Option}.
   * @return the field of the {@code Property} the given {@code CustomOption} is referring to, or {@code null} if one 
   * cannot be found.
   */
  public Property fieldFrom(CustomOption option) {
    SimplePropertyRef ref = option.getPropertyField();
    return (ref == null) ? null : ref.getProperty();
  }
  
  /**
   * Indicates whether the given object is an "extend message" and its name matches the one specified in the given 
   * option type.
   * @param o the object to check.
   * @param optionType the type of option we are interested in.
   * @return {@code true} if the given object is an "extend message" and its name matches the one specified in the given 
   * option type; {@code false} otherwise.
   */
  public boolean isExtendingOptionMessage(EObject o, OptionType optionType) {
    if (!(o instanceof ExtendMessage)) return false;
    Message message = messageFrom((ExtendMessage) o);
    if (message == null) return false;
    return optionType.messageName().equals(message.getName());
  }

  private Message messageFrom(ExtendMessage extend) {
    MessageRef ref = extend.getMessage();
    return ref == null ? null : ref.getType();
  }
}

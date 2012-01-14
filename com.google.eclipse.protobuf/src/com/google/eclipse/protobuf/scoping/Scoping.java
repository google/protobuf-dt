/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Scoping {
  /**
   * Returns the descriptions of all <code>{@link ComplexType}</code>s available in scope. The given
   * <code>{@link MessageField}</code> may be using one of the returned {@code ComplexType}s as its type.
   * @param field the given {@code MessageField}.
   * @return the descriptions of all <code>{@link ComplexType}</code>s available in scope.
   * @see MessageField#getType()
   * @see ComplexTypeLink#getTarget()
   */
  Collection<IEObjectDescription> complexTypesInScope(MessageField field);

  /**
   * Returns the descriptions of all <code>{@link ExtensibleType}</code>s available in scope. The given
   * <code>{@link TypeExtension}</code> may be using one of the returned {@code ExtensibleType}s as its extended type.
   * @param extension the given {@code TypeExtension}.
   * @return the descriptions of all the possible {@code ExtensibleType}s available in scope.
   * @see TypeExtension#getType()
   * @see ExtensibleTypeLink#getTarget()
   */
  Collection<IEObjectDescription> extensibleTypesInScope(TypeExtension extension);

  /**
   * Returns the descriptions of all <code>{@link Message}</code>s available in scope. The given
   * <code>{@link Rpc}</code> may be using the returned {@code Message}s, one as its argument type and another one as
   * its return type.
   * @param rpc
   * @return the descriptions of all {@code Message}s available in scope.
   * @see Rpc#getArgType()
   * @see Rpc#getReturnType()
   * @see MessageLink#getTarget()
   */
  Collection<IEObjectDescription> messagesInScope(Rpc rpc);

  /**
   * Returns the descriptions of all <code>{@link IndexedElement}</code>s available in scope. The given
   * <code>{@link AbstractCustomOption}</code> may be using one of the returned {@code IndexedElement}s as its source.
   * @param option the given {@code AbstractCustomOption}.
   * @return the descriptions of all {@code IndexedElement}s available in scope.
   * @see CustomOption#getSource()
   * @see CustomFieldOption#getSource()
   * @see OptionSource#getTarget()
   */
  Collection<IEObjectDescription> indexedElementsInScope(AbstractCustomOption option);

  /**
   * Returns the descriptions of all <code>{@link IndexedElement}</code>s available in scope. The given
   * <code>{@link AbstractCustomOption}</code> may be using some of the returned {@code IndexedElement}s as its fields.
   * @param option the given {@code AbstractCustomOption}.
   * @return the descriptions of all {@code IndexedElement}s available in scope.
   * @see CustomOption#getFields()
   * @see CustomFieldOption#getFields()
   * @see MessageOptionField
   * @see OptionField#getTarget()
   */
  Collection<IEObjectDescription> messageFieldsInScope(AbstractCustomOption option);

  /**
   * Returns the descriptions of all <code>{@link IndexedElement}</code>s available in scope. The given
   * <code>{@link AbstractCustomOption}</code> may be using <em>extensions</em> of the returned {@code IndexedElement}s
   * as its fields.
   * @param option the given {@code AbstractCustomOption}.
   * @return the descriptions of all {@code IndexedElement}s available in scope.
   * @see CustomOption#getFields()
   * @see CustomFieldOption#getFields()
   * @see ExtensionOptionField
   * @see OptionField#getTarget()
   * @see TypeExtension#getType()
   */
  Collection<IEObjectDescription> extensionFieldsInScope(AbstractCustomOption option);

  Collection<IEObjectDescription> allPossibleNamesOfNormalFieldsOf(ComplexValue value);

  Collection<IEObjectDescription> allPossibleNamesOfExtensionFieldsOf(ComplexValue value);
}

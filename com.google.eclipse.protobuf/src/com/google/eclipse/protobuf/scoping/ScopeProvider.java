/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import java.util.Collection;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Stream;
import com.google.eclipse.protobuf.protobuf.TypeExtension;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface ScopeProvider {
  Collection<IEObjectDescription> potentialComplexTypesFor(MessageField field);

  Collection<IEObjectDescription> potentialExtensibleTypesFor(TypeExtension extension);

  Collection<IEObjectDescription> potentialMessagesFor(Rpc rpc);

  Collection<IEObjectDescription> potentialMessagesFor(Stream stream);

  Collection<IEObjectDescription> potentialSourcesFor(AbstractCustomOption option);

  Collection<IEObjectDescription> potentialMessageFieldsFor(AbstractCustomOption option);

  Collection<IEObjectDescription> potentialExtensionFieldsFor(AbstractCustomOption option);

  Collection<IEObjectDescription> potentialNormalFieldNames(ComplexValue value);

  Collection<IEObjectDescription> potentialExtensionFieldNames(ComplexValue value);
}

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

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Scoping {
  Collection<IEObjectDescription> allPossibleTypesFor(MessageField field);

  Collection<IEObjectDescription> allPossibleTypesFor(TypeExtension extension);

  Collection<IEObjectDescription> allPossibleMessagesFor(Rpc rpc);

  Collection<IEObjectDescription> allPossibleSourcesOf(AbstractCustomOption option);

  Collection<IEObjectDescription> allPossibleNormalFieldsOf(AbstractCustomOption option);

  Collection<IEObjectDescription> allPossibleExtensionFieldsOf(AbstractCustomOption option);

  Collection<IEObjectDescription> allPossibleNamesOfNormalFieldsOf(ComplexValue value);

  Collection<IEObjectDescription> allPossibleNamesOfExtensionFieldsOf(ComplexValue value);
}

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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Scoping {

  Collection<IEObjectDescription> findScope(CustomOption o);

  Collection<IEObjectDescription> findScope(CustomFieldOption o);

  Collection<IEObjectDescription> findScope(OptionMessageFieldSource s);

  Collection<IEObjectDescription> findScope(OptionExtendMessageFieldSource s);

  Collection<IEObjectDescription> findMessageFieldScope(CustomOption o);

  Collection<IEObjectDescription> findMessageFieldScope(CustomFieldOption o);

  Collection<IEObjectDescription> findExtendMessageFieldScope(CustomOption o);

  Collection<IEObjectDescription> findExtendMessageFieldScope(CustomFieldOption o);

  Collection<IEObjectDescription> findTypeScope(EObject o);

  Collection<IEObjectDescription> findMessageScope(EObject o);
}

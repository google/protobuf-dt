/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.Import;

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
interface SearchDelegate {

  boolean continueSearchOneLevelHigher(Object target);
  
  Collection<IEObjectDescription> fromProtoDescriptor(Import anImport, Object criteria);

  Collection<IEObjectDescription> descriptions(Object target, Object criteria);

  Collection<IEObjectDescription> descriptions(Object target, Object criteria, int level);
}

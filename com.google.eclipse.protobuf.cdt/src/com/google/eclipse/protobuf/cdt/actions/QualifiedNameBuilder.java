/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.xtext.naming.QualifiedName;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
interface QualifiedNameBuilder {
  QualifiedName createQualifiedNameFrom(ASTNode node);
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf

import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor
import org.eclipse.xtext.GeneratedMetamodel
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClass

/*
 * Author: alruiz@google.com (Alex Ruiz)
 */
class ProtobufEcorePostProcessor implements IXtext2EcorePostProcessor {
  override process(GeneratedMetamodel metamodel) {
    metamodel.EPackage.process;
  }

  def process(EPackage p) {
    for (c : p.EClassifiers.filter(typeof(EClass))) {
      if (c.name == "Literal") {
        c.setDefaultValueOfLiteralIndex
      }
    }
  }
  
  def setDefaultValueOfLiteralIndex(EClass c) {
    for (attribute : c.EAllAttributes) {
      if (attribute.name == "index") {
        attribute.defaultValue = -1
        return
      }
    }
  }
}
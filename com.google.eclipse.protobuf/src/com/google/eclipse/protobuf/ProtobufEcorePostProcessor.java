/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.GeneratedMetamodel;
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufEcorePostProcessor implements IXtext2EcorePostProcessor {

  @Override public void process(GeneratedMetamodel metamodel) {
    EPackage p = metamodel.getEPackage();
    for (EClassifier c : p.getEClassifiers()) {
      if (c instanceof EClass && "Literal".equals(c.getName())) {
        processLiteralClass((EClass) c);
      }
    }
  }

  private void processLiteralClass(EClass c) {
    for (EAttribute a : c.getEAllAttributes()) {
      if ("index".equals(a.getName())) {
        a.setDefaultValue(-1L);
        break;
      }
    }
  }
}

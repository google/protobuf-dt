/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.eclipse.protobuf.protobuf.Modifier.OPTIONAL;
import static com.google.eclipse.protobuf.protobuf.Modifier.REPEATED;
import static com.google.eclipse.protobuf.protobuf.Modifier.REQUIRED;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.ENUM;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.EXTENSIONS;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.GROUP;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.LITERAL;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.OPTION;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PACKAGE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.RPC;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SERVICE;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.STREAM;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SYNTAX;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.TYPE_EXTENSION;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Keyword;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Modifier;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.inject.Singleton;

/**
 * Registry of all images used in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Images {
  private static final String IMAGE_FILE_NAME_FORMAT = "%s.gif";
  private static final String DEFAULT_IMAGE = "empty.gif";

  private static final Set<String> IMAGES = newHashSet();
  static {
    addImages(OPTIONAL, REPEATED, REQUIRED);
    addImages(ENUM, TYPE_EXTENSION, EXTENSIONS, GROUP, IMPORT, LITERAL, MESSAGE, OPTION, PACKAGE, RPC, SERVICE, STREAM,
        SYNTAX);
    addImages("imports", "options");
  }

  private static void addImages(Modifier...modifiers) {
    for (Modifier m : modifiers) {
      addImage(imageNameFrom(m));
    }
  }

  private static void addImages(EClass...eClasses) {
    for (EClass c : eClasses) {
      addImage(imageNameFrom(c));
    }
  }

  private static void addImages(String...imageNames) {
    for (String s : imageNames) {
      addImage(s);
    }
  }

  private static void addImage(String imageName) {
    IMAGES.add(imageFileName(imageName));
  }

  public String imageFor(Object o) {
    String imageName = null;
    if (o instanceof Keyword) {
      Keyword keyword = (Keyword) o;
      imageName = keyword.getValue().toLowerCase();
    } else if (o instanceof CommonKeyword) {
      CommonKeyword keyword = (CommonKeyword) o;
      imageName = keyword.toString();
    } else if (o instanceof String) {
      imageName = (String) o;
    } else if (o instanceof MessageField) {
      MessageField field = (MessageField) o;
      Modifier modifier = field.getModifier();
      imageName = imageNameFrom(modifier);
    } else if (o instanceof Option) {
      imageName = imageNameFrom(OPTION);
    } else if (o instanceof Import) {
      imageName = imageNameFrom(IMPORT);
    } else if (o instanceof EClass) {
      EClass eClass = (EClass) o;
      imageName = imageNameFrom(eClass);
    } else if (o instanceof EObject) {
      EObject modelObject = (EObject) o;
      imageName = imageNameFrom(modelObject.eClass());
    }
    String imageFileName = null;
    if (imageName != null) {
      imageFileName = imageFileName(imageName);
    }
    return (IMAGES.contains(imageFileName)) ? imageFileName : defaultImage();
  }

  private static String imageNameFrom(Modifier modifier) {
    return modifier.getName().toLowerCase();
  }

  private static String imageNameFrom(EClass eClass) {
    return eClass.getName().toLowerCase();
  }

  private static String imageFileName(String imageName) {
    return String.format(IMAGE_FILE_NAME_FORMAT, imageName);
  }

  public String defaultImage() {
    return DEFAULT_IMAGE;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static com.google.eclipse.protobuf.protobuf.Modifier.*;
import static java.util.Arrays.asList;

import java.util.*;

import org.eclipse.xtext.Keyword;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

/**
 * Registry of all images used in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Images {

  private static final String GIF_EXTENSION = ".gif";
  private static final String DEFAULT_IMAGE = "empty.gif";

  private static final Map<Modifier, String> IMAGES_BY_MODIFIER = new HashMap<Modifier, String>();
  static {
    IMAGES_BY_MODIFIER.put(OPTIONAL, "property-opt.gif");
    IMAGES_BY_MODIFIER.put(REPEATED, "property-rep.gif");
    IMAGES_BY_MODIFIER.put(REQUIRED, "property-req.gif");
  }

  private static final Map<Class<?>, String> IMAGES_BY_TYPE = new HashMap<Class<?>, String>();
  static {
    IMAGES_BY_TYPE.put(Enum.class, "enum.gif");
    IMAGES_BY_TYPE.put(ExtendMessage.class, "extend.gif");
    IMAGES_BY_TYPE.put(Group.class, "group.gif");
    IMAGES_BY_TYPE.put(Import.class, "import.gif");
    IMAGES_BY_TYPE.put(Literal.class, "literal.gif");
    IMAGES_BY_TYPE.put(Message.class, "message.gif");
    IMAGES_BY_TYPE.put(Option.class, "option.gif");
    IMAGES_BY_TYPE.put(Package.class, "package.gif");
    IMAGES_BY_TYPE.put(Rpc.class, "rpc.gif");
    IMAGES_BY_TYPE.put(Service.class, "service.gif");
    IMAGES_BY_TYPE.put(Syntax.class, "syntax.gif");
  }

  private static final List<String> STANDALONE_IMAGES = asList("extensions.gif", "imports.gif", "options.gif");

  public String imageFor(Object o) {
    if (o instanceof Keyword) {
      Keyword k = (Keyword) o;
      return imageFor(k);
    }
    if (o instanceof Property) {
      Property p = (Property) o;
      return imageFor(p.getModifier());
    }
    if (o instanceof String) {
      return o + GIF_EXTENSION;
    }
    return imageFor(o.getClass());
  }

  public String imageFor(Class<?> type) {
    String image = IMAGES_BY_TYPE.get(type);
    if (image != null) return image;
    Class<?>[] interfaces = type.getInterfaces();
    if (interfaces == null || interfaces.length != 1) return DEFAULT_IMAGE;
    return imageFor(interfaces[0]);
  }

  private String imageFor(Keyword k) {
    String value = k.getValue();
    Modifier m = Modifier.getByName(value);
    String image = IMAGES_BY_MODIFIER.get(m);
    if (image != null) return image;
    String imageName = value + GIF_EXTENSION;
    if (IMAGES_BY_TYPE.containsValue(imageName) || STANDALONE_IMAGES.contains(imageName)) return imageName;
    return DEFAULT_IMAGE;
  }

  private String imageFor(Modifier m) {
    String image = IMAGES_BY_MODIFIER.get(m);
    if (image != null) return image;
    return "property.gif";
  }

  public String defaultImage() {
    return DEFAULT_IMAGE;
  }
}

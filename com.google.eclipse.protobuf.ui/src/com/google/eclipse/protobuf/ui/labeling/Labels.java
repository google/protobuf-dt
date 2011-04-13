/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.labeling;

import static com.google.eclipse.protobuf.scoping.SimpleImportUriResolver.URI_PREFIX;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.util.Properties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Registry of commonly used text in the 'Protocol Buffer' editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Labels {

  @Inject private Properties properties;

  private static final String LITERAL_FORMAT = "%s [%d]";
  private static final String PROPERTY_FORMAT = "%s [%d] : %s";

  public String labelFor(Object o) {
    if (o instanceof Import) {
      Import i = (Import) o;
      return labelFor(i);
    }
    if (o instanceof Literal) {
      Literal l = (Literal) o;
      return labelFor(l);
    }
    if (o instanceof Property) {
      Property p = (Property) o;
      return labelFor(p);
    }
    if (o instanceof Protobuf) {
      Protobuf p = (Protobuf) o;
      return labelFor(p);
    }
    return null;
  }

  private String labelFor(Import i) {
    String uri = i.getImportURI();
    if (uri == null || !uri.startsWith(URI_PREFIX)) return uri;
    return uri.substring(URI_PREFIX.length());
  }

  private String labelFor(Literal l) {
    return String.format(LITERAL_FORMAT, l.getName(), l.getIndex());
  }

  private String labelFor(Property p) {
    return String.format(PROPERTY_FORMAT, p.getName(), p.getIndex(), properties.nameOfTypeIn(p));
  }

  private String labelFor(Protobuf p) {
    // TODO show this text till I figure out how to hide 'Protobuf' node in outline view
    return "Protocol Buffer";
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.unmodifiableCollection;

import java.io.*;
import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.EObjectFinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Protobuf elements accessible to any .proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Globals {

  private Protobuf root;

  private boolean initialized;
  private Map<String, Property> fileOptions = new LinkedHashMap<String, Property>();
  private Enum optimizedMode;

  @Inject EObjectFinder finder;

  @Inject public Globals(IParser parser) {
    try {
      XtextResource resource = new XtextResource(URI.createURI(""));
      IParseResult result = parser.parse(new InputStreamReader(globalScopeContents(), "UTF-8"));
      root = (Protobuf) result.getRootASTElement();
      resource.getContents().add(root);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to parse global scope", e);
    }
  }

  private static InputStream globalScopeContents() {
    return new StringInputStream(globalProto());
  }

  private static String globalProto() {
    StringBuilder proto = new StringBuilder();
    proto.append("message FileOptions {")
         .append("  optional string java_package = 1;")
         .append("  optional string java_outer_classname = 8;")
         .append("  optional bool java_multiple_files = 10 [default=false];")
         .append("  optional bool java_generate_equals_and_hash = 20 [default=false];")
         .append("  enum OptimizeMode {")
         .append("    SPEED = 1;")
         .append("    CODE_SIZE = 2;")
         .append("    LITE_RUNTIME = 3;")
         .append("  }")
         .append("  optional OptimizeMode optimize_for = 9 [default=SPEED];")
         .append("  optional bool cc_generic_services = 16 [default=false];")
         .append("  optional bool java_generic_services = 17 [default=false];")
         .append("  optional bool py_generic_services = 18 [default=false];")
         .append("  repeated UninterpretedOption uninterpreted_option = 999;")
         .append("  extensions 1000 to max;")
         .append("}");
    return proto.toString();
  }

  private void init() {
    if (initialized) return;
    initialized = true;
    Message m = fileOptionsMessage();
    for (MessageElement e : m.getElements()) {
      if (e instanceof Property) {
        addFileOption((Property) e);
        continue;
      }
      if (e instanceof Enum && "OptimizeMode".equals(e.getName())) {
        optimizedMode = (Enum) e;
        continue;
      }
    }
  }

  private Message fileOptionsMessage() {
    for (ProtobufElement e : root.getElements()) {
      if (!(e instanceof Message)) continue;
      Message m = (Message) e;
      if ("FileOptions".equals(m.getName())) return m;
    }
    return null;
  }

  private void addFileOption(Property p) {
    fileOptions.put(p.getName(), p);
  }

  public Collection<Property> fileOptions() {
    init();
    return unmodifiableCollection(fileOptions.values());
  }

  public Enum optimizedMode() {
    init();
    return optimizedMode;
  }

  public boolean isOptimizeForOption(Option option) {
    init();
    return "optimize_for".equals(option.getName());
  }

  public Property lookupFileOption(String name) {
    init();
    return fileOptions.get(name);
  }
}

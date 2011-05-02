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
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.io.*;
import java.util.*;

import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
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

  private final Map<String, Property> fileOptions = new LinkedHashMap<String, Property>();
  private Enum optimizedMode;

  /**
   * Creates a new </code>{@link Globals}</code>.
   * @param parser the grammar parser.
   */
  @Inject public Globals(IParser parser) {
    try {
      XtextResource resource = new XtextResource(createURI("globals.proto"));
      IParseResult result = parser.parse(new InputStreamReader(globalScopeContents(), "UTF-8"));
      root = (Protobuf) result.getRootASTElement();
      resource.getContents().add(root);
      initContents();
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

  private void initContents() {
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
    for (Message m : getAllContentsOfType(root, Message.class))
      if ("FileOptions".equals(m.getName())) return m;
    throw new IllegalStateException("Unable to find message 'FileOptions'");
  }

  private void addFileOption(Property p) {
    fileOptions.put(p.getName(), p);
  }

  /**
   * Returns all the file-level options available. These are the options defined in 
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the file-level options available.
   */
  public Collection<Property> fileOptions() {
    return unmodifiableCollection(fileOptions.values());
  }

  /**
   * Returns the {@code enum} "OptimizeMode" (defined in {@code google/protobuf/descriptor.proto}. More details can be 
   * found <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.) 
   * @return the {@code enum} "OptimizeMode."
   */
  public Enum optimizedMode() {
    return optimizedMode;
  }

  /**
   * Indicates whether the given option is the "OptimizeMode" one (defined in {@code google/protobuf/descriptor.proto}. 
   * More details can be found 
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.) 
   * @param option the given option.
   * @return {@code true} if the given option is the "OptimizeMode" one, {@code false} otherwise.
   */
  public boolean isOptimizeForOption(Option option) {
    if (option == null) return false;
    return "optimize_for".equals(option.getName());
  }

  /**
   * Looks up a file-level option per name. File-level options are defined in {@code google/protobuf/descriptor.proto}
   * (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param name the name of the option to look for.
   * @return the option whose name matches the given one or {@code null} if a matching option is not found.
   */
  public Property lookupFileOption(String name) {
    return fileOptions.get(name);
  }
}

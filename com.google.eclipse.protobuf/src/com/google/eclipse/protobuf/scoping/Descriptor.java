/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.scoping.OptionType.*;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.EcoreUtil2.*;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import java.io.*;
import java.util.*;

import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

/**
 * Contains the elements from descriptor.proto (provided with protobuf's library.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Descriptor {

  private Protobuf root;

  private final Map<OptionType, Map<String, Property>> options = new HashMap<OptionType, Map<String, Property>>();
   
  private Enum optimizedMode;
  private Enum cType;

  /**
   * Creates a new </code>{@link Descriptor}</code>.
   * @param parser the grammar parser.
   */
  @Inject public Descriptor(IParser parser) {
    addOptionTypes();
    try {
      XtextResource resource = new XtextResource(createURI("descriptor.proto"));
      IParseResult result = parser.parse(new InputStreamReader(globalScopeContents(), "UTF-8"));
      root = (Protobuf) result.getRootASTElement();
      resource.getContents().add(root);
      resolveLazyCrossReferences(resource, NullImpl);
      initContents();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to parse global scope", e);
    }
  }

  private void addOptionTypes() {
    for (OptionType type : OptionType.values())
      options.put(type, new LinkedHashMap<String, Property>());
  }
  
  private static InputStream globalScopeContents() {
    return new StringInputStream(descriptorContents());
  }

  private static String descriptorContents() {
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
         .append("  extensions 1000 to max;")
         .append("}")
         .append("message FieldOptions {")
         .append("  optional CType ctype = 1 [default = STRING];")
         .append("  enum CType {")
         .append("    STRING = 0;")
         .append("    CORD = 1;")
         .append("    STRING_PIECE = 2;")
         .append("  }")
         .append("  optional bool packed = 2;")
         .append("  optional bool deprecated = 3 [default=false];")
         .append("  extensions 1000 to max;")
         .append("}");
    return proto.toString();
  }

  private void initContents() {
    for (Message m : getAllContentsOfType(root, Message.class)) {
      if (isFileOptionsMessage(m)) initFileOptions(m);
      else if (isFieldOptionsMessage(m)) initFieldOptions(m);
    }
  }
  
  private boolean isFileOptionsMessage(Message m) {
    return "FileOptions".equals(m.getName());
  }

  private boolean isFieldOptionsMessage(Message m) {
    return "FieldOptions".equals(m.getName());
  }

  private void initFileOptions(Message fileOptionsMessage) {
    for (MessageElement e : fileOptionsMessage.getElements()) {
      if (e instanceof Property) {
        addFileOption((Property) e);
        continue;
      }
      if (isEnumWithName(e, "OptimizeMode")) {
        optimizedMode = (Enum) e;
        continue;
      }
    }
  }
  
  private void addFileOption(Property p) {
    addOption(FILE, p);
  }

  private void initFieldOptions(Message fieldOptionsMessage) {
    for (MessageElement e : fieldOptionsMessage.getElements()) {
      if (e instanceof Property) {
        addFieldOption((Property) e);
        continue;
      }
      if (isEnumWithName(e, "CType")) {
        cType = (Enum) e;
        continue;
      }
    }
  }
  
  private void addFieldOption(Property p) {
    addOption(FIELD, p);
  }
  
  private void addOption(OptionType type, Property p) {
    options.get(type).put(p.getName(), p);
  }
  
  private boolean isEnumWithName(MessageElement e, String name) {
    if (!(e instanceof Enum)) return false;
    Enum anEnum = (Enum) e;
    return name.equals(anEnum.getName());
  }
  
  /**
   * Returns all the file-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the file-level options available.
   */
  public Collection<Property> fileOptions() {
    return optionsOfType(FILE);
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
   * Indicates whether the given option is the "optimize_for" one (defined in {@code google/protobuf/descriptor.proto}.
   * More details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return {@code true} if the given option is the "optimize_for" one, {@code false} otherwise.
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
    return lookupOption(FILE, name);
  }

  /**
   * Returns all the field-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the field-level options available.
   */
  public Collection<Property> fieldOptions() {
    return optionsOfType(FIELD);
  }

  private Collection<Property> optionsOfType(OptionType type) {
    return unmodifiableCollection(options.get(type).values());
  }
  
  /**
   * Looks up a field-level option per name. Field-level options are defined in {@code google/protobuf/descriptor.proto}
   * (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param name the name of the option to look for.
   * @return the option whose name matches the given one or {@code null} if a matching option is not found.
   */
  public Property lookupFieldOption(String name) {
    return lookupOption(FIELD, name);
  }
  
  private Property lookupOption(OptionType type, String name) {
    return options.get(type).get(name);
  }

  /**
   * Indicates whether the given option is the "ctype" one (defined in {@code google/protobuf/descriptor.proto}.
   * More details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return {@code true} if the given option is the "ctype" one, {@code false} otherwise.
   */
  public boolean isCTypeOption(FieldOption option) {
    if (option == null) return false;
    return "ctype".equals(option.getName());
  }
  
  /**
   * Returns the {@code enum} "CType" (defined in {@code google/protobuf/descriptor.proto}. More details can be
   * found <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return the {@code enum} "CType."
   */
  public Enum cType() {
    return cType;
  }
}

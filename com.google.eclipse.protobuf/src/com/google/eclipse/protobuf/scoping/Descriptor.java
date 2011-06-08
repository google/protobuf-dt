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
import static com.google.eclipse.protobuf.util.Closeables.close;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.EcoreUtil2.*;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.inject.Inject;

import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.resource.XtextResource;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Contains the elements from descriptor.proto (provided with protobuf's library.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Descriptor {

  private static final String DESCRIPTOR_URI = "platform:/plugin/com.google.eclipse.protobuf/descriptor.proto";

  private final Map<OptionType, Map<String, Property>> options = new HashMap<OptionType, Map<String, Property>>();
   
  private Protobuf root;
  private Enum optimizedMode;
  private Enum cType;

  @Inject public Descriptor(IParser parser) {
    addOptionTypes();
    InputStreamReader reader = null;
    try {
      XtextResource resource = new XtextResource(createURI(DESCRIPTOR_URI));
      reader = new InputStreamReader(globalScopeContents(), "UTF-8");
      IParseResult result = parser.parse(reader);
      root = (Protobuf) result.getRootASTElement();
      resource.getContents().add(root);
      resolveLazyCrossReferences(resource, NullImpl);
      initContents();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to parse descriptor.proto", e);
    } finally {
      close(reader);
    }
  }

  private void addOptionTypes() {
    for (OptionType type : OptionType.values())
      options.put(type, new LinkedHashMap<String, Property>());
  }
  
  private static InputStream globalScopeContents() throws IOException {
    URL url = new URL(DESCRIPTOR_URI);
    return url.openConnection().getInputStream();
  }

  private void initContents() {
    for (Message m : getAllContentsOfType(root, Message.class)) {
      if (isFileOptionsMessage(m)) initFileOptions(m);
      else if (isMessageOptionsMessage(m)) initMessageOptions(m);
      else if (isFieldOptionsMessage(m)) initFieldOptions(m);
    }
  }
  
  private boolean isFileOptionsMessage(Message m) {
    return "FileOptions".equals(m.getName());
  }

  private boolean isMessageOptionsMessage(Message m) {
    return "MessageOptions".equals(m.getName());
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

  private void initMessageOptions(Message messageOptionsMessage) {
    for (MessageElement e : messageOptionsMessage.getElements()) {
      if (e instanceof Property) {
        addMessageOption((Property) e);
        continue;
      }
    }
  }
  
  private void addMessageOption(Property p) {
    addOption(MESSAGE, p);
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
    if (shouldIgnore(p)) return;
    options.get(type).put(p.getName(), p);
  }
  
  private boolean shouldIgnore(Property property) {
    return "uninterpreted_option".equals(property.getName());
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
   * Looks up an option per name, as defined in {@code google/protobuf/descriptor.proto}
   * (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param name the name of the option to look for.
   * @return the option whose name matches the given one or {@code null} if a matching option is not found.
   */
  public Property lookupOption(String name) {
    Property p = lookupOption(FILE, name);
    if (p == null) lookupOption(MESSAGE, name);
    return p;
  }

  /**
   * Returns all the message-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the message-level options available.
   */
  public Collection<Property> messageOptions() {
    return optionsOfType(MESSAGE);
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
   * Returns the enum type of the given option, only if the given option is defined in 
   * {@code google/protobuf/descriptor.proto} and its type an enum (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return the enum type of the given option or {@code null} if the type of the given option is not enum.
   */
  public Enum enumTypeOf(Option option) {
    if (isOptimizeForOption(option)) return optimizedMode;
    return null;
  }

  private boolean isOptimizeForOption(Option option) {
    if (option == null) return false;
    return "optimize_for".equals(option.getName());
  }

  /**
   * Returns the enum type of the given option, only if the given option is defined in 
   * {@code google/protobuf/descriptor.proto} and its type an enum (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return the enum type of the given option or {@code null} if the type of the given option is not enum.
   */
  public Enum enumTypeOf(FieldOption option) {
    if (isCTypeOption(option)) return cType;
    return null;
  }

  private boolean isCTypeOption(FieldOption option) {
    if (option == null) return false;
    return "ctype".equals(option.getName());
  }
}

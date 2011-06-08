/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PROPERTY__TYPE;
import static com.google.eclipse.protobuf.scoping.OptionType.*;
import static com.google.eclipse.protobuf.util.Closeables.close;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.EcoreUtil2.*;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.Inject;

import org.eclipse.xtext.nodemodel.INode;
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
public class ProtoDescriptor implements IProtoDescriptor {

  private static final String DESCRIPTOR_URI = "platform:/plugin/com.google.eclipse.protobuf/descriptor.proto";
  private static final Map<String, OptionType> OPTION_DEFINITION_BY_NAME = new HashMap<String, OptionType>();
  
  static {
    OPTION_DEFINITION_BY_NAME.put("FileOptions", FILE);
    OPTION_DEFINITION_BY_NAME.put("MessageOptions", MESSAGE);
    OPTION_DEFINITION_BY_NAME.put("FieldOptions", FIELD);
  }
  
  private final Map<OptionType, Map<String, Property>> optionsByType = new HashMap<OptionType, Map<String, Property>>();
  private final Map<String, Enum> enumsByName = new HashMap<String, Enum>();

  private Protobuf root;

  private final ModelNodes nodes;

  @Inject public ProtoDescriptor(IParser parser, ModelNodes nodes) {
    this.nodes = nodes;
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
    } catch (Throwable t) {
      t.printStackTrace();
      throw new IllegalStateException("Unable to parse descriptor.proto", t);
    } finally {
      close(reader);
    }
  }

  private void addOptionTypes() {
    for (OptionType type : OptionType.values())
      optionsByType.put(type, new LinkedHashMap<String, Property>());
  }

  private static InputStream globalScopeContents() throws IOException {
    URL url = new URL(DESCRIPTOR_URI);
    return url.openConnection().getInputStream();
  }

  private void initContents() {
    for (Message m : getAllContentsOfType(root, Message.class)) {
      OptionType type = OPTION_DEFINITION_BY_NAME.get(m.getName());
      if (type == null) continue;
      initOptions(m, type);
    }
  }

  private void initOptions(Message optionGroup, OptionType type) {
    for (MessageElement e : optionGroup.getElements()) {
      if (e instanceof Property) {
        addOption((Property) e, type);
        continue;
      }
      if (e instanceof Enum) {
        Enum anEnum = (Enum) e;
        enumsByName.put(anEnum.getName(), anEnum);
      }
    }
  }

  private void addOption(Property optionDefinition, OptionType type) {
    if (shouldIgnore(optionDefinition)) return;
    optionsByType.get(type).put(optionDefinition.getName(), optionDefinition);
  }

  private boolean shouldIgnore(Property property) {
    return "uninterpreted_option".equals(property.getName());
  }

  /** {@inheritDoc} */
  public Collection<Property> fileOptions() {
    return optionsOfType(FILE);
  }

  /** {@inheritDoc} */
  public Property lookupOption(String name) {
    return lookupOption(name, FILE, MESSAGE);
  }
  
  public Property lookupOption(String name, OptionType...types) {
    for (OptionType type : types) {
      Property p = lookupOption(name, type);
      if (p != null) return p;
    }
    return null;
  }

  /** {@inheritDoc} */
  public Property lookupFieldOption(String name) {
    return lookupOption(name, FIELD);
  }

  private Property lookupOption(String name, OptionType type) {
    return optionsByType.get(type).get(name);
  }

  /** {@inheritDoc} */
  public Collection<Property> messageOptions() {
    return optionsOfType(MESSAGE);
  }

  /** {@inheritDoc} */
  public Collection<Property> fieldOptions() {
    return optionsOfType(FIELD);
  }

  private Collection<Property> optionsOfType(OptionType type) {
    return unmodifiableCollection(optionsByType.get(type).values());
  }

  /** {@inheritDoc} */
  public Enum enumTypeOf(Option option) {
    String name = option.getName();
    return enumTypeOf(lookupOption(name));
  }

  /** {@inheritDoc} */
  public Enum enumTypeOf(FieldOption option) {
    String name = option.getName();
    return enumTypeOf(lookupFieldOption(name));
  }

  private Enum enumTypeOf(Property p) {
    if (p == null) return null;
    INode node = nodes.firstNodeForFeature(p, PROPERTY__TYPE);
    if (node == null) return null;
    String typeName = node.getText();
    return (isEmpty(typeName)) ? null : enumsByName.get(typeName.trim());
  }
}

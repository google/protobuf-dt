/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__TYPE;
import static com.google.eclipse.protobuf.scoping.OptionType.findOptionTypeForLevelOf;
import static com.google.eclipse.protobuf.util.Encodings.UTF_8;
import static java.util.Collections.*;
import static org.eclipse.xtext.EcoreUtil2.*;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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
public class ProtoDescriptor {
  private static final Map<String, OptionType> OPTION_DEFINITION_BY_NAME = newHashMap();

  static {
    populateMap();
  }

  private static void populateMap() {
    for (OptionType type : OptionType.values()) {
      OPTION_DEFINITION_BY_NAME.put(type.messageName(), type);
    }
  }

  private final List<ComplexType> allTypes = newArrayList();
  private final Map<OptionType, Map<String, MessageField>> optionsByType = newHashMap();
  private final Map<String, Enum> enumsByName = newHashMap();

  private Protobuf root;

  private final String importUri;
  private final INodes nodes;
  private final XtextResource resource;

  ProtoDescriptor(String importUri, URI location, IParser parser, INodes nodes) {
    this.importUri = importUri;
    this.nodes = nodes;
    addOptionTypes();
    InputStreamReader reader = null;
    try {
      resource = new XtextResource(location);
      reader = new InputStreamReader(contents(location), UTF_8);
      IParseResult result = parser.parse(reader);
      root = (Protobuf) result.getRootASTElement();
      resource.getContents().add(root);
      resolveLazyCrossReferences(resource, NullImpl);
      initContents();
    } catch (Throwable t) {
      throw new IllegalStateException("Unable to parse descriptor.proto", t);
    } finally {
      closeQuietly(reader);
    }
  }

  /**
   * Returns the contents of the descriptor file at the given location.
   * @param descriptorLocation the location of the descriptor file.
   * @return the contents of the descriptor file.
   * @throws IOException if something goes wrong.
   */
  protected InputStream contents(URI descriptorLocation) throws IOException {
    URL url = new URL(descriptorLocation.toString());
    return url.openConnection().getInputStream();
  }

  private void addOptionTypes() {
    for (OptionType type : OptionType.values()) {
      optionsByType.put(type, new LinkedHashMap<String, MessageField>());
    }
  }

  private void initContents() {
    allTypes.addAll(getAllContentsOfType(root, ComplexType.class));
    for (ComplexType t : allTypes) {
      if (!(t instanceof Message)) {
        continue;
      }
      Message m = (Message) t;
      OptionType type = OPTION_DEFINITION_BY_NAME.get(m.getName());
      if (type == null) {
        continue;
      }
      initOptions(m, type);
    }
  }

  private void initOptions(Message optionGroup, OptionType type) {
    for (MessageElement e : optionGroup.getElements()) {
      if (e instanceof MessageField) {
        addOption((MessageField) e, type);
        continue;
      }
      if (e instanceof Enum) {
        Enum anEnum = (Enum) e;
        String name = anEnum.getName();
        enumsByName.put(name, anEnum);
      }
    }
  }

  private void addOption(MessageField optionSource, OptionType type) {
    if (shouldIgnore(optionSource)) {
      return;
    }
    String name = optionSource.getName();
    optionsByType.get(type).put(name, optionSource);
  }

  private boolean shouldIgnore(MessageField field) {
    return "uninterpreted_option".equals(field.getName());
  }

  /**
   * Returns the options available for the given option or option container. The returned options are defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param o the given option or option container.
   * @return the options available for the given option or option container, or an empty collection if the are not any
   *         options available.
   */
  public Collection<MessageField> availableOptionsFor(EObject o) {
    EObject target = o;
    if (target instanceof NativeOption) {
      target = target.eContainer();
    }
    OptionType type = findOptionTypeForLevelOf(target);
    if (type == null) {
      return emptyList();
    }
    return optionsOfType(type);
  }

  @VisibleForTesting Collection<MessageField> optionsOfType(OptionType type) {
    return unmodifiableCollection(optionsByType.get(type).values());
  }

  /**
   * Returns the enum type of the given field, only if the given field is defined in
   * {@code google/protobuf/descriptor.proto} and its type is enum (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param field the given field.
   * @return the enum type of the given field or {@code null} if the type of the given field is not enum.
   */
  public Enum enumTypeOf(MessageField field) {
    if (field == null) {
      return null;
    }
    INode node = nodes.firstNodeForFeature(field, MESSAGE_FIELD__TYPE);
    if (node == null) {
      return null;
    }
    String typeName = node.getText();
    return (isNullOrEmpty(typeName)) ? null : enumByName(typeName.trim());
  }

  @VisibleForTesting Enum enumByName(String qualifiedName) {
    String[] segments = qualifiedName.split("\\.");
    if (segments == null || segments.length == 0) {
      return null;
    }
    return enumsByName.get(segments[segments.length - 1]);
  }

  /**
   * Returns all types in descriptor.proto.
   * @return all types in descriptor.proto.
   */
  public List<ComplexType> allTypes() {
    return unmodifiableList(allTypes);
  }

  public XtextResource resource() {
    return resource;
  }

  /**
   * Returns the URI to use when importing descriptor.proto.
   * @return the URI to use when importing descriptor.proto.
   */
  public String importUri() {
    return importUri;
  }

  @VisibleForTesting MessageField option(String name, OptionType type) {
    Map<String, MessageField> optionByName = optionsByType.get(type);
    return (optionByName != null) ? optionByName.get(name) : null;
  }
}

/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.unmodifiableList;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import java.util.*;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

/**
 * Utility methods to find elements in a parser proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class ModelFinder {

  /**
   * Returns the message type of the given property, only if the type of the given property is a message.
   * @param p the given property.
   * @return the message type of the given property or {@code null} if the type of the given property is not message.
   */
  public Message messageTypeOf(Property p) {
    Type type = typeOf(p);
    return (type instanceof Message) ? (Message) type : null;
  }
  
  /**
   * Returns the enum type of the given property, only if the type of the given property is an enum.
   * @param p the given property.
   * @return the enum type of the given property or {@code null} if the type of the given property is not enum.
   */
  public Enum enumTypeOf(Property p) {
    Type type = typeOf(p);
    return (type instanceof Enum) ? (Enum) type : null;
  }
  
  private Type typeOf(Property p) {
    AbstractTypeRef r = p.getType();
    if (!(r instanceof TypeRef)) return null;
    return ((TypeRef) r).getType();
  }

  /**
   * Returns the scalar type of the given property, only if the type of the given property is a scalar.
   * @param p the given property.
   * @return the scalar type of the given property or {@code null} if the type of the given property is not a scalar.
   */
  public ScalarType scalarTypeOf(Property p) {
    AbstractTypeRef aTypeRef = (p).getType();
    if (aTypeRef instanceof ScalarTypeRef)
      return ((ScalarTypeRef) aTypeRef).getScalar();
    return null;
  }

  /**
   * Returns the package of the proto file containing the given object.
   * @param o the given object.
   * @return the package of the proto file containing the given object or {@code null} if the proto file does not have a
   * package.
   */
  public Package packageOf(EObject o) {
    Protobuf root = rootOf(o);
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Package) return (Package) e;
    }
    return null;
  }

  /**
   * Returns the root element of the proto file containing the given element.
   * @param o the given element.
   * @return the root element of the proto file containing the given element.
   */
  public Protobuf rootOf(EObject o) {
    EObject current = o;
    while (!(current instanceof Protobuf)) current = current.eContainer();
    return (Protobuf) current;
  }

  /**
   * Returns all the import definitions in the given proto.
   * @param root the given proto.
   * @return all the import definitions in the given proto.
   */
  public List<Import> importsIn(Protobuf root) {
    List<Import> imports = new ArrayList<Import>();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Import) imports.add((Import) e);
    }
    return unmodifiableList(imports);
  }

  /**
   * Returns all the public import definitions in the given proto.
   * @param root the given proto.
   * @return all the public import definitions in the given proto.
   */
  public List<Import> publicImportsIn(Protobuf root) {
    List<Import> imports = new ArrayList<Import>();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof PublicImport) imports.add((Import) e);
    }
    return unmodifiableList(imports);
  }

  /**
   * Returns the root element of the given resource.
   * @param resource the given resource.
   * @return the root element of the given resource, or {@code null} if the given resource does not have a root element.
   */
  public Protobuf rootOf(Resource resource) {
    if (resource instanceof XtextResource) {
      EObject root = ((XtextResource) resource).getParseResult().getRootASTElement();
      return (Protobuf) root;
    }
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof Protobuf) return (Protobuf) next;
    }
    return null;
  }

  public Collection<Property> propertiesOf(Message message) {
    List<Property> properties = new ArrayList<Property>();
    for (MessageElement e :message.getElements()) {
      if (e instanceof Property) properties.add((Property) e);
    }
    return unmodifiableList(properties);
  }
}

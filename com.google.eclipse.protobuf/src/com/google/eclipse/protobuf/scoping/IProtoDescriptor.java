/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

import java.util.Collection;

/**
 * Contains the elements from descriptor.proto (provided with protobuf's library.)
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface IProtoDescriptor {

  /**
   * Returns all the file-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the file-level options available.
   */
  public abstract Collection<Property> fileOptions();

  /**
   * Looks up an option per name, as defined in {@code google/protobuf/descriptor.proto}
   * (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param name the name of the option to look for.
   * @return the option whose name matches the given one or {@code null} if a matching option is not found.
   */
  public abstract Property lookupOption(String name);

  /**
   * Returns all the message-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the message-level options available.
   */
  public abstract Collection<Property> messageOptions();

  /**
   * Returns all the field-level options available. These are the options defined in
   * {@code google/protobuf/descriptor.proto} (more details can be found
   * <a href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @return all the field-level options available.
   */
  public abstract Collection<Property> fieldOptions();

  /**
   * Looks up a field-level option per name. Field-level options are defined in {@code google/protobuf/descriptor.proto}
   * (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param name the name of the option to look for.
   * @return the option whose name matches the given one or {@code null} if a matching option is not found.
   */
  public abstract Property lookupFieldOption(String name);

  /**
   * Returns the enum type of the given option, only if the given option is defined in 
   * {@code google/protobuf/descriptor.proto} and its type an enum (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return the enum type of the given option or {@code null} if the type of the given option is not enum.
   */
  public abstract Enum enumTypeOf(Option option);

  /**
   * Returns the enum type of the given option, only if the given option is defined in 
   * {@code google/protobuf/descriptor.proto} and its type an enum (more details can be found <a
   * href=http://code.google.com/apis/protocolbuffers/docs/proto.html#options" target="_blank">here</a>.)
   * @param option the given option.
   * @return the enum type of the given option or {@code null} if the type of the given option is not enum.
   */
  public abstract Enum enumTypeOf(FieldOption option);

}

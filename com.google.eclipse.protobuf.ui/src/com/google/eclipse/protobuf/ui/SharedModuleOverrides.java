package com.google.eclipse.protobuf.ui;

import com.google.eclipse.protobuf.ui.resource.ProtobufStorage2UriMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import org.eclipse.xtext.ui.resource.IStorage2UriMapper;

public class SharedModuleOverrides extends AbstractModule {
  @Override
  protected void configure() {
    bind(IStorage2UriMapper.class).to(ProtobufStorage2UriMapper.class).in(Scopes.SINGLETON);
  }
}

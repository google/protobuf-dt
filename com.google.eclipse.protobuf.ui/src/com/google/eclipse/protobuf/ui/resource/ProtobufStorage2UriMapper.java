package com.google.eclipse.protobuf.ui.resource;

import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtext.ui.resource.IStorage2UriMapperJdtExtensions;
import org.eclipse.xtext.ui.resource.Storage2UriMapperImpl;
import org.eclipse.xtext.util.Pair;

import java.util.Collections;
import java.util.Map;

public class ProtobufStorage2UriMapper extends Storage2UriMapperImpl 
    implements IStorage2UriMapperJdtExtensions {
  @Override
  public Map<URI, IStorage> getAllEntries(IPackageFragmentRoot root) {
    return Collections.emptyMap();
  }

  @Override
  public Pair<URI, URI> getURIMapping(IPackageFragmentRoot root) throws JavaModelException {
    return null;
  }
}
